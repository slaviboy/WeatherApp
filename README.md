# WeatherApp 
Weather App written in Kotlin, using Jetpack Compose

<p align="center">
    <img src="https://github.com/slaviboy/RepositoryImages/blob/main/apps/weather_app_home.png?raw=true" alt="Image"   />
</p>
 
## How to
To run the app you need key for the weather API. The app is using https://openweathermap.org/ for retriving data about the weather. To get free API key register on this site, then open the **local.properties** and add the following line. Just change **your_api_key** with your API key.
```
WEATHER_APP_KEY = your_api_key
```

_*You can check the demo [here](https://www.youtube.com/watch?v=O0efgKq0xOI).*_

Data about the cities is retrived from [local database](https://github.com/slaviboy/WeatherApp/tree/master/app/src/main/assets/database) that way it is faster when searching for particular city. I have generated the database as a **.db** file(SQLite DB), you can open or edit the data using this free tool [DB Browser for SQLite](https://github.com/sqlitebrowser/sqlitebrowser).
## To do
1) Add effects for different weather: Rain, Snow, Sunny, Mist...
2) Add Weather widget fro the app
