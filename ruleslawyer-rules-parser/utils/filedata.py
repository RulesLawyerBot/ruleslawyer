class FileData:
    def __init__(self, data):
        self.data = data
        self.currentLine = 0
        self.size = len(data)

    def has_line(self):
        return self.size > self.currentLine

    def next_line(self):
        self.currentLine += 1
        return self.data[self.currentLine-1]
