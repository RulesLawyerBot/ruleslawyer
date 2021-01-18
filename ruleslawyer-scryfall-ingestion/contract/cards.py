from json import dumps


class Card:
    def __init__(self, card_name, mana_cost, type_line, oracle, rulings, card_set, legalities, price):
        self.card_name = card_name
        self.mana_cost = mana_cost
        self.type_line = type_line
        self.oracle = oracle
        self.rulings = rulings
        self.sets = [card_set]
        self.legalities = legalities
        self.totalPrice = price

    def __repr__(self):
        return '{"cardName":"' + self.card_name + '","manaCost":"' + self.mana_cost + '","typeLine":"' + self.type_line + '","oracleText":' + dumps(self.oracle) + ',"rulings":' + dumps(self.rulings) + ',"sets":' + dumps(self.sets) + ',"legalities":' + dumps(self.legalities) + ',"totalPrice":' + str(self.totalPrice) + '}'

    def add_set(self, set, price):
        self.sets.append(set)
        self.totalPrice += price
