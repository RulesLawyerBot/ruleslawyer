import sys
sys.path.append("..")
from utils.csv_to_json import csv_to_json
from utils.simple_io import write
from utils.simple_io import clear
from utils.simple_io import read_csv


def main():
    cr_raw = read_csv("CR-parsed.csv")
    clear("CR-parsed.json")
    write("CR-parsed.json", csv_to_json(cr_raw))

    cr_raw = read_csv("CRG-parsed.csv")
    clear("CRG-parsed.json")
    write("CRG-parsed.json", csv_to_json(cr_raw))


if __name__ == "__main__":
    main()
