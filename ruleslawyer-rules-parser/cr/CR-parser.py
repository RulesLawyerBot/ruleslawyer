import sys
sys.path.append("..")
from utils.simple_io import write
from utils.simple_io import clear
from utils.simple_io import open_file
from utils.filedata import FileData
from utils.unprintable_remover import replace_unprintable
from contract.rules import RuleSuperHeader
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
            if len(rule_builder) != 0:
                print(rule_builder)
                header.subrules[0].subrules.append(Rule(rule_builder.strip()))
                rule_builder = ""

        citation = line.split(" ")[0]
        if len(citation) == 2:
            continue
        if len(citation) == 4: # superheader
            if superheader:
                output.append(superheader)
            superheader = RuleSuperHeader(line, [])
        elif is_int(citation[4:-1]) and citation[-1] == ".": # header/subheader
            subheader_text = line[line.index(" ")+1:]
            header = RuleHeader(citation, [RuleSubHeader(subheader_text, [])])
            if superheader:
                superheader.subrules.append(header)
        else: # base rule
            if len(rule_builder) == 0:
                rule_builder = line
            else:
                rule_builder = rule_builder + " EOL " + line

    print(output)
    clear("CR-parsed.json")
    write("CR-parsed.json", output)

    # Glossary
    output = RuleHeader("Glossary", [])
    line_builder = ""
    current_rule = None
    while True:
        line = file.next_line().replace('“', '"').replace('”', '"').replace("’", "'")
        if line == "Credits":
            break
        if len(line) == 0:
            if current_rule:
                current_rule.subrules.append(Rule(line_builder.strip()))
                output.subrules.append(current_rule)
                line_builder = ""
            current_rule = None
        else:
            if not current_rule:
                current_rule = RuleSubHeader(line, [])
            else:
                line_builder = line_builder + " " + line

    print(output)
    clear("CRG-parsed.json")
    write("CRG-parsed.json", [output])


if __name__ == "__main__":
    main()