# coding=windows-1252

import sys
sys.path.append("..")
from utils.simple_io import clear
from utils.simple_io import open_file
from utils.simple_io import write_csv
from utils.filedata import FileData
from utils.unprintable_remover import replace_unprintable
from contract.rules import RuleHeader
from contract.rules import RuleSubHeader
from contract.rules import Rule


def is_int(val):
    try:
        num = int(val)
    except ValueError:
        return False
    return True


def main():
    file = FileData(open_file("CR-input.txt"))
    output = []

    while file.next_line() != "Credits":
        True

    # main CR
    rule_builder = ""
    superheader = None
    header = None
    while True:
        line = replace_unprintable(file.next_line())
        if line == "Glossary":
            output.append(superheader)
            break

        if len(line) == 0: # make rule
            citation = rule_builder.split(" ")[0]
            if len(rule_builder) != 0:
                header.subrules.append(Rule(rule_builder.strip(), ([] if citation == "Example:" or not citation else [citation])))
                rule_builder = ""

        citation = line.split(" ")[0]

        if len(citation) == 2:
            continue
        if len(citation) == 4: # superheader
            if superheader:
                output.append(superheader)
            superheader = RuleHeader(line, [], ([citation[:-1]] if citation else []))
        elif is_int(citation[4:-1]) and citation[-1] == ".": # header/subheader
            header = RuleSubHeader(line, [], ([citation[:-1]] if citation else []))
            if superheader:
                superheader.subrules.append(header)
        else: # base rule
            if len(rule_builder) == 0:
                rule_builder = line
            else:
                rule_builder = rule_builder + " EOL " + line

    csv_output = []
    for rule in output:
        csv_output = csv_output + rule.toArray()

    clear("CR-parsed.csv")
    write_csv("CR-parsed.csv", csv_output)

    # Glossary
    output = RuleHeader("Glossary", [], [])
    line_builder = ""
    current_rule = None
    while True:
        line = file.next_line()
        if line == "Credits":
            break
        if len(line) == 0:
            if current_rule:
                current_rule.subrules.append(Rule(line_builder.strip(), []))
                output.subrules.append(current_rule)
                line_builder = ""
            current_rule = None
        else:
            if not current_rule:
                try:
                    citation_string = line[0:line.index(" (")]
                except ValueError:
                    citation_string = line
                current_rule = RuleSubHeader(line, [], (citation_string.replace('"', "").split(", ") if citation_string else []))
            else:
                line_builder = line_builder + " " + line

    clear("CRG-parsed.csv")
    write_csv("CRG-parsed.csv", output.toArray())


if __name__ == "__main__":
    main()
