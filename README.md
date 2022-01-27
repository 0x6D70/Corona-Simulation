# Corona Extension feat Tscharnig

Simulation of a classroom

## Idea:

- simulate everyday situation (every day starts automatically
- 5 classes + teachers chamber
- the teachers meet after each lesson in the teacher’s chamber
- students/teachers with positive tests go home and come back after 5 days of quarantine

## Sequence of Events:

- everyone goes from entry to their seat places
- one teacher goes to every class / one lesson
- teacher goes to teachers chamber
- last two steps repeat
- everyone goes to entry / day over
- everything repeats


## Settings: 
- the user can change following settings:
    - percent infected at start in % 
    - how usefull are the tests in % (how many students are sent home)
    - percentage vaccinated in % 
    - toggle (% of people who break the rules)
    
- static settings (rules): 
    - every day new infective people: 10 % of percentage at start (e.g. 20% at start, 2% every day)
    - every day 30% of infected people become infective
    - Number of Lessons per day: 3
    - vaccinations: 50% lower chance of getting infected, if infected 50% less chance of        infecting someone else
    - no infections at entry    
    - Contacts till:
        - Suspected: 1
        - Infected: 2
        - Infective 3

## Possible extentions

- a toilet room / supermarket / smoking place 
- Counter: How many are currently healthy/suspected/infected/infective
- new Settings: 
    - Slider for Number of Lessons per day
    - Length of quarantine
    - How affective are vaccinations
    - if not enough teacher, classes go home

