# Filey
A file management system.
Features
Create/Delete Files and Directories

Navigate directories

Perform file operations:

Open/Read files

Move/Rename files

Write at position

Append to file

Show file details (size & creation date)

Move content within file

Truncate part of a file

Beautiful JavaFX UI

Persistent data storage using a binary file

🏗️ System Architecture
csharp
Copy code
[JavaFX Frontend (FileManagementApp.java)]
        |
        | (Uses subprocess to run Python backend)
        ↓
[Python Backend]
├── interface.py  → CLI for processing commands
└── main.py       → Core logic, virtual filesystem & file operations
📦 Directory Structure
graphql
Copy code
FileManagementSystem/
│
├── FileManagementApp.java   # JavaFX GUI Application
├── main.py                  # Core backend logic (FileSystem, FileObject classes)
├── interface.py             # Python CLI to bridge Java ↔ Python
├── filesystem_data.pkl      # (Auto-generated) Persistent storage for virtual file system
└── README.md                # Project overview & usage instructions
🛠️ How It Works
1. JavaFX GUI (FileManagementApp.java)
Launches a user-friendly UI with:

Tree view for directories and files

Input forms for operations

Buttons to execute tasks

2. Backend Execution
When you perform an operation in the UI, it executes a Python command like:

bash
Copy code
python interface.py write_at file.txt 10 "Hello"
Java captures the output and updates the GUI accordingly.

3. Python Backend
main.py holds:

FileObject: Manages content, size, timestamps

FileSystem: Handles directories, navigation, file map, and command execution

interface.py parses command-line arguments and calls the appropriate method

4. Persistent Data
The system saves all files and folders to filesystem_data.pkl

It is automatically loaded and updated on each command

💻 How to Run
🧩 Prerequisites
Java 17+

Python 3.7+

JavaFX (if not bundled with your JDK)

🔧 Run the System
Start the JavaFX App

Compile and run FileManagementApp.java from your IDE or terminal.

Perform Operations via GUI

Use the tree to select a file

Choose an operation from the dropdown

Fill required fields

Click Execute to run the command

Backend Python is Automatically Invoked

No manual Python launch required

Communication via subprocess

