from json import dumps


class Card:
    def __init__(self, card_name, mana_cost, type_line, oracle, rulings, card_set):
        self.card_name = card_name
        self.mana_cost = mana_cost
        self.type_line = type_line
        self.oracle = oracle
        self.rulings = rulings
        self.sets = [card_set]

    def __repr__(self):
        return '{"cardName":"' + self.card_name + '","manaCost":"' + self.mana_cost + '","typeLine":"' + self.type_line + '","oracleText":' + dumps(self.oracle) + ',"rulings":' + dumps(self.rulings) + ',"sets":' + dumps(self.sets) +'}'

    def add_set(self, set):
        self.sets.append(set)