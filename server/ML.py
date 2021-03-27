import os
import pandas as pd
from sklearn.preprocessing import OneHotEncoder
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier

encoder = OneHotEncoder()
forest_clf = RandomForestClassifier(n_estimators=100, n_jobs=-1, min_samples_leaf=1)


# print(soc_data.head())


def getResult(data):
    # home team win
    if data['FT Team 1'] > data['FT Team 2']:
        return 1
    # home team loss
    elif data['FT Team 1'] < data['FT Team 2']:
        return -1
    # tie
    elif data['FT Team 1'] == data['FT Team 2']:
        return 0


def train():
    soc_data = pd.read_csv('soccer.csv')
    if 'result' in soc_data:
        soc_data.drop(columns=['result'], inplace=True)
    soc_data['result'] = soc_data.apply(getResult, axis=1)

    soc_data.drop(
        columns=['Round', 'Date', 'FT', 'HT', 'Year', 'Country', 'FT Team 1', 'FT Team 2', 'HT Team 1', 'HT Team 2',
                 'GGD',
                 'Team 1 (pts)', 'Team 2 (pts)'], inplace=True)

    y = soc_data['result']
    X = soc_data.drop(columns=['result'])

    X_tr = encoder.fit_transform(X)

    X_soc_train, X_soc_test, y_soc_train, y_soc_test = train_test_split(X_tr, y, test_size=0.2, random_state=42)

    forest_clf.fit(X_soc_train, y_soc_train)
    print('fit succsess')


if __name__ == '__main__':
    train()
    # team1 = encoder.transform([['Liverpool FC ', 'Sheffield Wednesday FC ']])
    # predict = forest_clf.predict(team1)
    # print(predict)

