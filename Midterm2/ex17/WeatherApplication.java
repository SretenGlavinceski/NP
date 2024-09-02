package Midterm2.ex17;

import java.util.*;

public class WeatherApplication {

    public static void main(String[] args) {
        WeatherDispatcher weatherDispatcher = new WeatherDispatcher();

        CurrentConditionsDisplay currentConditions = new CurrentConditionsDisplay(weatherDispatcher);
        ForecastDisplay forecastDisplay = new ForecastDisplay(weatherDispatcher);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");
            weatherDispatcher.setMeasurements(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
            if(parts.length > 3) {
                int operation = Integer.parseInt(parts[3]);
                if(operation==1) {
                    weatherDispatcher.remove(forecastDisplay);
                }
                if(operation==2) {
                    weatherDispatcher.remove(currentConditions);
                }
                if(operation==3) {
                    weatherDispatcher.register(forecastDisplay);
                }
                if(operation==4) {
                    weatherDispatcher.register(currentConditions);
                }

            }
        }
    }
}

interface WeatherEntity {
    void update ();
}

class CurrentConditionsDisplay implements WeatherEntity {
    private float temperature;
    private float humidity;
    WeatherDispatcher weatherDispatcher;

    public CurrentConditionsDisplay(WeatherDispatcher dispatcher) {
        this.weatherDispatcher = dispatcher;
        weatherDispatcher.register(this);
    }

    @Override
    public void update() {
        this.temperature = weatherDispatcher.getTemperature();
        this.humidity = weatherDispatcher.getHumidity();

        System.out.printf("Temperature: %.1fF\nHumidity: %.1f%%%n",
                temperature,
                humidity);
    }
}

class ForecastDisplay implements WeatherEntity {
    private float currPressure = 0.0f;
    private float lastPressure;
    WeatherDispatcher weatherDispatcher;
    public ForecastDisplay(WeatherDispatcher dispatcher) {
        this.weatherDispatcher = dispatcher;
        weatherDispatcher.register(this);
    }

    @Override
    public void update() {
        this.lastPressure = currPressure;
        this.currPressure = weatherDispatcher.getPressure();

        if (currPressure > lastPressure) {
            System.out.println("Forecast: Improving\n");
        } else if (currPressure == lastPressure) {
            System.out.println("Forecast: Same\n");
        } else if (currPressure < lastPressure) {
            System.out.println("Forecast: Cooler\n");
        }
    }
}

class WeatherDispatcher {
    private final Set<WeatherEntity> weatherEntities;
    private float temperature;
    private float humidity;
    private float pressure;

    public WeatherDispatcher() {
        this.weatherEntities = new HashSet<>();
    }

    public void setMeasurements(float temperature, Float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;

        if (weatherEntities.isEmpty())
            System.out.println();
        else
            weatherEntities.forEach(WeatherEntity::update);
    }
    public void register (WeatherEntity weatherEntity) {
        weatherEntities.add(weatherEntity);
    }
    public void remove (WeatherEntity weatherEntity) {
        weatherEntities.remove(weatherEntity);
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }

}