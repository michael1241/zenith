#! /usr/bin/env python

#plot average rating by time of day

import pandas as pd

import matplotlib.pyplot as plt
import seaborn as sns
sns.set_theme()

with open('games', 'rb') as f:
    games = pd.read_pickle(f)

games['averageelo'] = (games['whiteelo'] + games['blackelo'])/2
av = games[['day', 'hour', 'averageelo']]
av = av.groupby(['day', 'hour']).mean().unstack().transpose()

days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
hours = [f'{hour:04}' for hour in range(0,2400,100)]


sns.heatmap(av, annot=True, fmt=".4g", xticklabels=days, yticklabels=hours)
ax = plt.axes()
x_ax = ax.axes.get_xaxis()
x_ax.label.set_visible(False)
y_ax = ax.axes.get_yaxis()
y_ax.label.set_visible(False)
plt.show()
