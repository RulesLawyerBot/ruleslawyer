from contract.rules import RuleHeader
from contract.rules import RuleSubHeader
from contract.rules import Rule
import json


def csv_to_json(csv_raw):
    rule_objects = []
    for line in csv_raw:
        if line[1]:
            rule_objects.append(RuleHeader(format_rule(line[1]), [], format_citation(line[0])))
        elif line[2]:
            rule_objects[-1].subrules.append(RuleSubHeader(format_rule(line[2]), [], format_citation(line[0])))
        elif line[3]:
            rule_objects[-1].subrules[-1].subrules.append(Rule(format_rule(line[3]), format_citation(line[0])))
        else:
            raise RuntimeError("Error in csv to json")
    return rule_objects


def format_rule(rule):
    return rule.replace("\\", "")


def format_citation(citation):
    return CitationArray(citation)


class CitationArray:
    def __init__(self, data):
        self.data = data

    def __repr__(self):
        return "[" + ",".join(['"' + keyword.strip("'\"") + '"' for keyword in self.data.strip("][").split("', '")]) + "]"
