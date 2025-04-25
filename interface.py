import sys
from main import FileSystem

fs = FileSystem()

args = sys.argv[1:]

if not args:
    print("No command provided")
    sys.exit(1)

command = args[0]

if command == "create":
    print(fs.create(args[1]))
elif command == "mkdir":
    print(fs.mkdir(args[1]))
elif command == "delete":
    print(fs.delete(args[1]))
elif command == "move":
    print(fs.move(args[1], args[2]))
elif command == "write_at":
    print(fs.write_at(args[1], args[2], " ".join(args[3:])))
elif command == "append":
    print(fs.append(args[1], " ".join(args[2:])))
elif command == "details":
    print(fs.details(args[1]))
elif command == "move_within_file":
    print(fs.move_within_file(args[1], args[2], args[3], args[4]))
elif command == "truncate":
    print(fs.truncate(args[1], args[2], args[3]))
elif command == "read":
    print(fs.read(args[1]))
elif command == "list":
    print(fs.list_files())
else:
    print("Invalid command")
