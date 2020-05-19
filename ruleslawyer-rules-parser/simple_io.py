def open_file(filename):
    """Returns the contents of the file as a list of strings."""
    f = open(filename, "r", encoding="utf-8")
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


def getPDF(filename):
    import tika
    tika.initVM()
    from tika import parser
    parsed = parser.from_file(filename)
    return parsed["content"].split("\n")