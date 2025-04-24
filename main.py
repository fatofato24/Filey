import os
import pickle

class FileObject:
    def __init__(self, name, content=""):
        self.name = name
        self.content = content
        self.open = False

    def write_to_file(self, text):
        self.content += text

    def write_at(self, index, text):
        if index < len(self.content):
            self.content = self.content[:index] + text + self.content[index + len(text):]
        else:
            self.content += " " * (index - len(self.content)) + text

    def read_from_file(self):
        return self.content

    def read_from(self, start, size):
        return self.content[start:start + size]

    def move_within_file(self, start, size, target):
        data = self.content[start:start + size]
        self.content = self.content[:start] + self.content[start + size:]
        self.content = self.content[:target] + data + self.content[target:]

    def truncate_file(self, size):
        self.content = self.content[:size]

class FileSystem:
    def __init__(self):
        self.structure = {'/': {}}  # root directory
        self.current_path = '/'
        self.files = {}
        self.load_data()

    def save_data(self):
        with open("filesystem.dat", "wb") as f:
            pickle.dump((self.structure, self.current_path, self.files), f)

    def load_data(self):
        if os.path.exists("filesystem.dat"):
            with open("filesystem.dat", "rb") as f:
                self.structure, self.current_path, self.files = pickle.load(f)
    def create(self, file_name):
        if file_name in self.files:
            print(f"File '{file_name}' already exists.")
            return
        self.files[file_name] = FileObject(file_name)
        self.structure[self.current_path][file_name] = 'file'
        self.save_data()
        print(f"File '{file_name}' created.")

    def delete(self, file_name):
        if file_name not in self.files:
            print(f"File '{file_name}' does not exist.")
            return
        del self.files[file_name]
        if file_name in self.structure[self.current_path]:
            del self.structure[self.current_path][file_name]
        self.save_data()
        print(f"File '{file_name}' deleted.")

    def mkdir(self, dir_name):
        if dir_name in self.structure[self.current_path]:
            print(f"Directory '{dir_name}' already exists.")
            return
        path = os.path.join(self.current_path, dir_name)
        self.structure[path] = {}
        self.structure[self.current_path][dir_name] = 'dir'
        self.save_data()
        print(f"Directory '{dir_name}' created.")

    def chdir(self, dir_name):
        if dir_name == "..":
            if self.current_path != "/":
                self.current_path = os.path.dirname(self.current_path.rstrip("/"))
                if self.current_path == "":
                    self.current_path = "/"
        else:
            path = os.path.join(self.current_path, dir_name)
            if path not in self.structure:
                print(f"Directory '{dir_name}' does not exist.")
                return
            self.current_path = path
        print(f"Changed directory to: {self.current_path}")
        self.save_data()
        
    def move(self, source, target):
        # Check if source exists
        if source not in self.structure[self.current_path]:
            print(f"File '{source}' not found in current directory.")
            return

        # Update structure only (we don't touch file content)
        self.structure[self.current_path][target] = self.structure[self.current_path].pop(source)
        self.files[target] = self.files.pop(source)
        self.files[target].name = target
        self.save_data()
        print(f"File renamed from '{source}' to '{target}'.")

    def open(self, file_name, mode='r'):
        if file_name not in self.files:
            print(f"File '{file_name}' does not exist.")
            return None
        file_obj = self.files[file_name]
        file_obj.open = True
        print(f"File '{file_name}' opened in mode '{mode}'.")
        return file_obj

    def close(self, file_name):
        if file_name not in self.files:
            print(f"File '{file_name}' does not exist.")
            return
        self.files[file_name].open = False
        self.save_data()
        print(f"File '{file_name}' closed.")

    def show_memory_map(self):
        print("\n📦 MEMORY MAP:")
        for file_name, file_obj in self.files.items():
            size = len(file_obj.content)
            print(f"{file_name} → Size: {size} chars, Open: {file_obj.open}")
        print()

        
