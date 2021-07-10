#! /usr/bin/env python

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
sns.set_theme()

with open('games', 'rb') as f:
    games = pd.read_pickle(f)

#filter by time control - blitz
#< 29s = UltraBullet < 179s = Bullet < 479s = Blitz < 1499s = Rapid ≥ 1500s = Classical
df = games[(games['control'] >=179) & (games['control'] <479)]

#simplify to look only at white and ignore elo
df = df.drop(['whiteelo', 'blackelo'], axis=1)
df = df.drop(['black', 'blackrc'], axis=1)

#for each player, remove the data from their most frequent bins (hours of play)
df = pd.melt(df, id_vars=['day', 'hour', 'whiterc'], value_vars=['white'], value_name='player', var_name='count')
df = df.groupby(['player', 'day', 'hour'], as_index=False).agg({
    'count': 'count',
    'whiterc': 'sum'
    })

m = df.groupby(['player'])['count'].transform('max')
df = df[~(df['count'] == m)]

df = df.drop(['player'], axis=1)


#average the rating change per bin across all players
#aim is to see whether players in specific time bins are weaker or stronger than other time bins
df = df.groupby(['day', 'hour']).agg({
    'count': 'sum',
    'whiterc': 'sum'
    })

df['change'] = df['whiterc'] / df['count']
df = df.drop(['count', 'whiterc'], axis=1)

df = df.unstack().transpose()

days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
hours = [f'{hour:04}' for hour in range(0,2400,100)]

sns.heatmap(df, annot=True, fmt=".4g", xticklabels=days, yticklabels=hours)
ax = plt.axes()
x_ax = ax.axes.get_xaxis()
x_ax.label.set_visible(False)
y_ax = ax.axes.get_yaxis()
y_ax.label.set_visible(False)
plt.show()

