import json
import string


def main():
    f = open("cards.json", "r", encoding="windows-1252")
    text = f.read()
    raw_data = json.loads(text)
    words = {}
    for card in raw_data:
        oracle = [x.lower().translate(str.maketrans('', '', string.punctuation)) for x in card["cardName"].split()]
        for word in oracle:
            if word in words:
                words[word] += 1
            else:
                words[word] = 1
    words = {k: v for k, v in sorted(words.items(), key=lambda item: item[1])}
    card_types = []
    for card in raw_data:
        types = [x.lower() for x in card["typeLine"].split()]
        if types not in card_types:
            card_types.append(types)
    for word in words:
        if words[word] > 5 and word not in card_types:
            print(word + " " + str(words[word]))


if __name__ == "__main__":
    main()