import os
import pickle
from datetime import datetime

DATA_FILE = "filesystem.pkl"

class FileObject:
    def __init__(self, name, content=""):
        self.name = name
        self.content = content
        self.created_at = datetime.now()

    def write_to_file(self, text):
        self.content += text

    def write_at(self, index, text):
        index = int(index)
        if index < len(self.content):
            self.content = self.content[:index] + text + self.content[index:]
        else:
            self.content += " " * (index - len(self.content)) + text

    def read_from_file(self):
        return self.content

    def move_within_file(self, start, size, target):
        start, size, target = int(start), int(size), int(target)
        snippet = self.content[start:start + size]
        self.content = self.content[:start] + self.content[start + size:]
        self.content = self.content[:target] + snippet + self.content[target:]

    def truncate(self, start, end):
        start, end = int(start), int(end)
        self.content = self.content[:start] + self.content[end:]

    def get_details(self):
        return f"Name: {self.name}\nCreated: {self.created_at}\nSize: {len(self.content)} bytes"

class FileSystem:
    def __init__(self):
        self.files = {}  # name: FileObject
        self.load_data()

    def save_data(self):
        with open(DATA_FILE, "wb") as f:
            pickle.dump(self.files, f)

    def load_data(self):
        if os.path.exists(DATA_FILE):
            with open(DATA_FILE, "rb") as f:
                self.files = pickle.load(f)

    def create(self, name):
        if name in self.files:
            return "File already exists"
        self.files[name] = FileObject(name)
        self.save_data()
        return "File created"

    def mkdir(self, name):
        return self.create(name)  # No actual directory structure yet

    def delete(self, name):
        if name in self.files:
            del self.files[name]
            self.save_data()
            return "Deleted"
        return "File not found"

    def move(self, old, new):
        if old in self.files:
            self.files[new] = self.files.pop(old)
            self.files[new].name = new
            self.save_data()
            return "Moved/Renamed"
        return "File not found"

    def write_at(self, name, index, text):
        if name in self.files:
            self.files[name].write_at(int(index), text)
            self.save_data()
            return "Written at position"
        return "File not found"

    def append(self, name, text):
        if name in self.files:
            self.files[name].write_to_file(text)
            self.save_data()
            return "Appended"
        return "File not found"

    def details(self, name):
        if name in self.files:
            return self.files[name].get_details()
        return "File not found"

    def move_within_file(self, name, start, target, size):
        if name in self.files:
            self.files[name].move_within_file(start, size, target)
            self.save_data()
            return "Moved text within file"
        return "File not found"

    def truncate(self, name, start, end):
        if name in self.files:
            self.files[name].truncate(start, end)
            self.save_data()
            return "Truncated file"
        return "File not found"

    def read(self, name):
        if name in self.files:
            return self.files[name].read_from_file()
        return "File not found"

    def list_files(self):
        return "\n".join(self.files.keys())
