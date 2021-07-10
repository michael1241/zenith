#! /usr/bin/env python

import chess.pgn
import datetime
import pandas as pd
import tqdm

class Game:
    def __init__(self, headers):
        self.white = headers.get('White')
        self.black = headers.get('Black')
        self.day, self.hour = self.getTimeBin(headers.get('UTCDate'), headers.get('UTCTime'))
        self.whiteelo = int(headers.get('WhiteElo'))
        self.blackelo = int(headers.get('BlackElo'))
        self.whiterc = int(headers.get('WhiteRatingDiff'))
        self.blackrc = int(headers.get('BlackRatingDiff'))
        self.control = self.timeConvert(headers.get('TimeControl'))

    def getTimeBin(self, date, time):
        day = datetime.datetime.strptime(date, "%Y.%m.%d").weekday()
        hour = datetime.datetime.strptime(time, "%H:%M:%S").hour
        return (day, hour)

    def timeConvert(self, control):
        t = control.split('+')
        return (int(t[0]) * 60) + (40 * int(t[1]))

def Parse(f):
    games = []
    pgn = open(f)
    with tqdm.tqdm(total=5015361) as pbar:
        while True:
            pbar.update(n=1)
            game = chess.pgn.read_game(pgn)
            if not game:
                break
            headers = game.headers
            try:
                g = Game(headers)
            except:
                continue
            games.append(g)
        return games

games = Parse('2016-02_reduced')
#games = Parse('test')

df = pd.DataFrame([vars(f) for f in games])

with open('games', 'wb') as f:
    df.to_pickle(f)
