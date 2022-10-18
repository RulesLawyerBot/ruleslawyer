# coding=windows-1252

def open_file(filename):
    """Returns the contents of the file as a list of strings."""
    f = open(filename, "r", encoding="windows-1252")
    data = f.read().split("\n")
    f.close()
    return data


def write(filename, message):
    """Appends a new line to the file."""
    f = open(filename, "a")
    f.write(str(message) + "\n")
    f.close()


def clear(filename):
    open(filename, "w").close()


def read_csv(filename):
    import csv
    output = []
    with open(filename, newline='') as csvfile:
        csvreader = csv.reader(csvfile, dialect="excel", delimiter=',', quotechar='"')
        for row in csvreader:
            try:
                output.append(row)
            except Error:
                errors = errors + 1
    return output


def write_csv(filename, content):
    import csv
    with open(filename, "w", newline='') as output:
        writer = csv.writer(output, dialect="excel", delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        for line in content:
            writer.writerow(line)


def get_PDF(filename):
    import tika
    tika.initVM()
    from tika import parser
    parsed = parser.from_file(filename)
    return parsed["content"].split("\n")