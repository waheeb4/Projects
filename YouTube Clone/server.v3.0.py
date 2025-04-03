import socket
import cv2
import pickle
import struct
import os
import subprocess
import time
import threading
from concurrent.futures import ThreadPoolExecutor


def get_ip_address():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(("8.8.8.8", 80))
        return s.getsockname()[0]
    finally:
        s.close()


def send_data_with_error_handling(client_socket, data):
    try:
        client_socket.sendall(data)
    except (ConnectionResetError, BrokenPipeError) as e:
        print(f"[ERROR] Connection error during send: {e}")
        return False
    return True


def list_videos():
    video_dir = "/home/adham-waheeb/Desktop/Python/Networks/Videos"
    videos_info = []
    for filename in os.listdir(video_dir):
        if filename.endswith((".mp4")):
            filepath = os.path.join(video_dir, filename)
            video_capture = cv2.VideoCapture(filepath)
            duration = video_capture.get(cv2.CAP_PROP_FRAME_COUNT) / video_capture.get(cv2.CAP_PROP_FPS)
            success, frame = video_capture.read()
            if success:
                _, thumbnail = cv2.imencode('.jpg', frame)
                videos_info.append((filename, thumbnail.tobytes(), duration))
            video_capture.release()
    return videos_info


def send_audio(client_socket, audio_path, start_time=0):
    chunk_size, channels, rate = 1024, 2, 44100
    ts = chunk_size / (rate * channels * 2)

    process = subprocess.Popen(
        ['ffmpeg', '-i', audio_path, '-f', 'wav', '-ss', str(start_time), '-'],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE
    )

    try:
        while True:
            data = process.stdout.read(chunk_size)
            if not data:
                break
            message = struct.pack("Q", len(data)) + b"AUDIO" + data
            if not send_data_with_error_handling(client_socket, message):
                break
            time.sleep(ts)
    except Exception as e:
        print(f"[ERROR] Error streaming audio: {e}")
    finally:
        process.stdout.close()
        process.stderr.close()
        process.terminate()
    
def send_video(client_socket, video_path):
    vid = cv2.VideoCapture(video_path)
    fps = vid.get(cv2.CAP_PROP_FPS)
    ts = 1 / fps
    try:
        while vid.isOpened():
            curtime = time.time()
            ret, frame = vid.read()
            if not ret:
                break
            _, buffer = cv2.imencode('.jpg', frame, [int(cv2.IMWRITE_JPEG_QUALITY), 75])
            frame_data = buffer.tobytes()
            message = struct.pack("Q", len(frame_data)) + b"VIDEO" + frame_data
            if not send_data_with_error_handling(client_socket, message):
                break
            elapsed_time = time.time() - curtime
            delay = ts - elapsed_time
            if delay > 0:
                time.sleep(delay)
    except Exception as e:
        print(f"[ERROR] Error streaming video: {e}")
    finally:
        vid.release()


def handle_client(video_socket, address, audio_socket):
    print(f"[INFO] Client connected from {address}")

    try:
        video_socket.settimeout(60)
        audio_socket.settimeout(60)
        while True:
            video_list = list_videos()
            data = pickle.dumps(video_list)
            data_size = struct.pack("Q", len(data))
            video_socket.sendall(data_size + data)

            video_name_data = video_socket.recv(1024)
            if not video_name_data:
                break

            video_request = video_name_data.decode('utf-8').strip()
            print(f"[INFO] Video request received: {video_request}")
            video_name = video_request
            video_path = os.path.join("/home/adham-waheeb/Desktop/Python/Networks/Videos", video_name)
            if os.path.exists(video_path):
                audio_thread = threading.Thread(target=send_audio, args=(audio_socket, video_path))
                video_thread = threading.Thread(target=send_video, args=(video_socket, video_path))
                audio_thread.start()
                video_thread.start()
                video_thread.join()
                audio_thread.join()
            else:
                video_socket.sendall(b"ERROR: Video not found")
            
            if video_request.startswith("SEEK:"):
                seek_time = int(video_request.split(":")[1])
                print(f"[INFO] Seek request received: {seek_time} seconds")
                video_thread = threading.Thread(target=send_video, args=(video_socket, video_path,seek_time))
                video_thread.start()

    except socket.timeout:
        print(f"[ERROR] Client connection timeout: {address}")
    except Exception as e:
        print(f"[ERROR] Exception handling client {address}: {e}")
    finally:
        video_socket.close()
        audio_socket.close()


def main():
    with ThreadPoolExecutor(max_workers=20) as executor:
        video_server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        audio_server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        host_ip = get_ip_address()
        video_port = 9999
        audio_port = 10000
        video_server_socket.bind((host_ip, video_port))
        audio_server_socket.bind((host_ip, audio_port))
        video_server_socket.listen(5)
        audio_server_socket.listen(5)

        print(f"[INFO] Video server started at {host_ip}:{video_port}")
        print(f"[INFO] Audio server started at {host_ip}:{audio_port}")

        while True:
            try:
                video_socket, video_addr = video_server_socket.accept()
                audio_socket, audio_addr = audio_server_socket.accept()
                executor.submit(handle_client, video_socket, video_addr, audio_socket)
            except KeyboardInterrupt:
                print("\n[INFO] Server shutting down...")
                video_server_socket.close()
                audio_server_socket.close()
                break
            except Exception as e:
                print(f"[ERROR] Exception in main loop: {e}")


if __name__ == "__main__":
    main()
