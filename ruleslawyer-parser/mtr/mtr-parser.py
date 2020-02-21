import sys
sys.path.append("..")
from simple_io import getPDF
from filedata import FileData


def main():
    file = FileData(getPDF("MTR.pdf"))
    while file.has_line():
        print(file.next_line())


if __name__ == "__main__":
    main()
