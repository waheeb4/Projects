import customtkinter as ctk
import socket
import pickle
import struct
import io
from PIL import Image, ImageTk
from tkinter import messagebox
import threading
import numpy as np
import pyaudio
import tkinter.messagebox as messagebox
import datetime
import cv2

class VideoClientApp:
    def __init__(self, root):
        self.root = root
        root.title("Video Streaming Client")
        root.geometry("800x600")
        root.configure(bg="#2E2E2E")

        self.video_connection = None
        self.playing_video = False
        self.video_thread = None
        self.stop_event = threading.Event()
        self.last_server_ip = ""
        self.pause_event = threading.Event()
        self.pause_event.set()
        self.current_time = 0

        self.setup_ui()

    def setup_ui(self):
        # Connection Frame
        self.connection_ui = ctk.CTkFrame(self.root)
        self.connection_ui.pack(pady=10, fill="x")

        self.server_ip_label = ctk.CTkLabel(self.connection_ui, text="Server IP:")
        self.server_ip_label.pack(side="left", padx=5)

        self.server_ip_entry = ctk.CTkEntry(self.connection_ui)
        self.server_ip_entry.pack(side="left", padx=5)

        self.connect_button = ctk.CTkButton(self.connection_ui, text="Connect", command=self.connect_to_server)
        self.connect_button.pack(side="left", padx=5)

        self.conn_status = ctk.CTkLabel(self.connection_ui, text="")
        self.conn_status.pack(side="left", padx=10)

        # Video Frame
        self.video_ui = ctk.CTkFrame(self.root)
        self.video_ui.pack(pady=10, fill="both", expand=True)

        self.canvas = ctk.CTkCanvas(self.video_ui, bg="#2E2E2E", highlightthickness=0)
        self.scrollbar = ctk.CTkScrollbar(self.video_ui, command=self.canvas.yview)
        self.scrollable_frame = ctk.CTkFrame(self.canvas)

        self.scrollable_frame.bind(
            "<Configure>",
            lambda e: self.canvas.configure(scrollregion=self.canvas.bbox("all"))
        )

        self.canvas.create_window((0, 0), window=self.scrollable_frame, anchor="nw")
        self.canvas.configure(yscrollcommand=self.scrollbar.set)

        self.canvas.pack(side="left", fill="both", expand=True)
        self.scrollbar.pack(side="right", fill="y")

        
    def connect_to_server(self):
        server_ip = self.server_ip_entry.get()
        video_port = 9999
        audio_port = 10000
        self.last_server_ip = server_ip

        try:
            # Video connection
            self.video_connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.video_connection.connect((server_ip, video_port))

            # Audio connection
            self.audio_connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.audio_connection.connect((server_ip, audio_port))


            # Receive video list and thumbnails
            data = b""
            payload_size = struct.calcsize("Q")
            while len(data) < payload_size:
                packet = self.video_connection.recv(4 * 1024)
                if not packet:
                    raise ConnectionError("Failed to receive data")
                data += packet

            packed_msg_size = data[:payload_size]
            data = data[payload_size:]
            msg_size = struct.unpack("Q", packed_msg_size)[0]

            while len(data) < msg_size:
                packet = self.video_connection.recv(4 * 1024)
                if not packet:
                    raise ConnectionError("Failed to receive full data")
                data += packet

            self.video_list = pickle.loads(data)

            for widget in self.scrollable_frame.winfo_children():
                widget.destroy()

            frame_width = 250
            frame_height = 250
            available_width = self.video_ui.winfo_width()
            num_columns = max(1, available_width // frame_width)

            print(f"Available width: {available_width}, Num columns: {num_columns}")

            row, col = 0, 0
            for video, thumbnail_data, duration in self.video_list:
                duration_str = str(datetime.timedelta(seconds=int(duration)))
                thumbnail_image = Image.open(io.BytesIO(thumbnail_data))
                thumbnail_image = thumbnail_image.resize((200, 150))
                thumbnail = ImageTk.PhotoImage(thumbnail_image)

                frame = ctk.CTkFrame(self.scrollable_frame, width=frame_width, height=frame_height)
                frame.grid(row=row, column=col, padx=5, pady=5, sticky="nsew")

                image_label = ctk.CTkLabel(frame, image=thumbnail, text="")
                image_label.image = thumbnail
                image_label.pack(side="top", pady=5)

                text_label = ctk.CTkLabel(frame, text=video, anchor="center", wraplength=250, font=("Arial", 12))
                text_label.pack(side="top", pady=5)

                duration_label = ctk.CTkLabel(frame, text=f"Duration: {duration_str}", anchor="center", font=("Arial", 10))
                duration_label.pack(side="top", pady=5)

                image_label.bind("<Double-1>", lambda e, v=video: self.play_video(v))
                text_label.bind("<Double-1>", lambda e, v=video: self.play_video(v))

                col += 1
                if col >= num_columns:
                    col = 0
                    row += 1

            for i in range(row + 1):
                self.scrollable_frame.grid_rowconfigure(i, weight=1)
            for j in range(num_columns):
                self.scrollable_frame.grid_columnconfigure(j, weight=1)

            
            self.conn_status.configure(text="Success, Connected to server!")
        except Exception as e:
            self.conn_status.configure(text="Error, Failed to connect to server")
            print(f"Failed to connect to server: {e}")

    def play_video(self, selected_video):
        if not selected_video:
            messagebox.showerror("Error", "Please select a video to play.")
            return

        self.video_ui.pack_forget()
        self.connection_ui.pack_forget()

        if hasattr(self, 'back_button'):
            self.back_button.destroy()
        if hasattr(self, 'video_player'):
            self.video_player.destroy()
        if hasattr(self, 'control_ui'):
            self.time_ui.destroy()

        self.control_frame = ctk.CTkFrame(self.root)
        self.control_frame.pack(pady=10, side="bottom", fill="x")

    
        self.play_btn = ctk.CTkButton(self.control_frame, text="Pause", command=self.toggle_play_pause)
        self.play_btn.pack(side="bottom", padx=5)

        self.back_button = ctk.CTkButton(self.root, text="Back", command=self.stop_video)
        self.back_button.pack(pady=10)

        self.video_player = ctk.CTkCanvas(self.root, bg="black", highlightthickness=0)
        self.video_player.pack(fill=ctk.BOTH, expand=True)

        self.time_ui = ctk.CTkFrame(self.root)
        self.time_ui.pack(side="bottom", fill="x")

        self.start_time = ctk.CTkLabel(self.time_ui, text="00:00:00")
        self.start_time.pack(side="left", padx=5)

        self.end_time = ctk.CTkLabel(self.time_ui, text="00:00:00")
        self.end_time.pack(side="right", padx=5)

        self.video_connection.sendall(selected_video.encode('utf-8'))

        self.stop_event.clear()
        self.playing_video = True

        self.current_video_duration = next((dur for vid, _, dur in self.video_list if vid == selected_video), 0)
        self.end_time.configure(text=str(datetime.timedelta(seconds=int(self.current_video_duration))))

        self.video_thread = threading.Thread(target=self.recieve_video)
        self.audio_thread = threading.Thread(target=self.recieve_audio)
        self.video_thread.start()
        self.audio_thread.start()

    def recieve_video(self):
        try:
            data = b""
            header_size = struct.calcsize("Q")
            while not self.stop_event.is_set():
                self.pause_event.wait()
                while len(data) < header_size:
                    packet = self.video_connection.recv(4 * 1024)
                    if not packet or self.stop_event.is_set():
                        return
                    data += packet

                packed_frame_size = data[:header_size]
                data = data[header_size:]
                frame_size = struct.unpack("Q", packed_frame_size)[0]

                while len(data) < frame_size + 5:
                    packet = self.video_connection.recv(4 * 1024)
                    if not packet or self.stop_event.is_set():
                        return
                    data += packet

                header = data[:5]
                frame_data = data[5:5 + frame_size]
                data = data[5 + frame_size:]

                if header != b"VIDEO":
                    continue

                frame = cv2.imdecode(np.frombuffer(frame_data, np.uint8), cv2.IMREAD_COLOR)
                if frame is None:
                    continue

                img = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                img = Image.fromarray(img)
                img = img.resize((self.video_player.winfo_width(), self.video_player.winfo_height()))
                img_tk = ImageTk.PhotoImage(img)

                self.video_player.create_image(0, 0, anchor="nw", image=img_tk)
                self.video_player.image = img_tk

                self.current_time += 1 / 30  # Assuming 30 FPS
                self.start_time.configure(text=str(datetime.timedelta(seconds=int(self.current_time))))

        except Exception as e:
            if not self.stop_event.is_set():
                messagebox.showerror("Error", f"Error streaming video: {e}")

    def recieve_audio(self):
        try:
            data = b""
            header_size = struct.calcsize("Q")
            p = pyaudio.PyAudio()
            stream = p.open(format=pyaudio.paInt16, channels=2, rate=44100, output=True)
            while not self.stop_event.is_set():
                self.pause_event.wait()
                while len(data) < header_size:
                    packet = self.audio_connection.recv(4 * 1024)
                    if not packet or self.stop_event.is_set():
                        return
                    data += packet

                packed_frame_size = data[:header_size]
                data = data[header_size:]
                frame_size = struct.unpack("Q", packed_frame_size)[0]

                while len(data) < frame_size + 5:
                    packet = self.audio_connection.recv(4 * 1024)
                    if not packet or self.stop_event.is_set():
                        return
                    data += packet

                header = data[:5]
                audio_data = data[5:5 + frame_size]
                data = data[5 + frame_size:]

                if header != b"AUDIO":
                    continue

                stream.write(audio_data)

            stream.stop_stream()
            stream.close()
            p.terminate()
        except Exception as e:
            if not self.stop_event.is_set():
                messagebox.showerror("Error", f"Error streaming audio: {e}")

    def toggle_play_pause(self):
        if self.playing_video:
            self.pause_event.clear()
            self.play_btn.configure(text="Play")
        else:
            self.pause_event.set()
            self.play_btn.configure(text="Pause")
        self.playing_video = not self.playing_video

    def stop_video(self):
        self.stop_event.set()
        if self.video_thread and self.video_thread.is_alive():
            self.video_thread.join(timeout=1)

        self.playing_video = False
        self.current_time= 0
        self.pause_event.set()
        self.video_player.delete("all")
        self.video_player.destroy()
        self.control_frame.destroy()
        self.start_time._label.configure(text="00:00:00")
        self.time_ui.destroy()
        self.back_button.destroy()

        self.connection_ui.pack(pady=10, fill=ctk.X)
        self.video_ui.pack(pady=10, fill=ctk.BOTH, expand=True)

        self.server_ip_entry.delete(0, ctk.END)
        self.server_ip_entry.insert(0, self.last_server_ip)
        self.connect_to_server()

if __name__ == "__main__":
    ctk.set_appearance_mode("dark")
    ctk.set_widget_scaling(1.0)
    root = ctk.CTk()
    app = VideoClientApp(root)
    root.mainloop()