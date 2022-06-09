
def replace_unprintable(line):
    line = line.strip()
    line = line.replace('“', '"').replace('”', '"')
    line = line.replace("’", "'").replace("‘", "'").replace("—", "-").replace("−", "-").replace("–", "-")
    line = line.replace("  ", " ").replace("●", "*").replace("", "*").replace("•", "*").replace("™", "")
    return line
