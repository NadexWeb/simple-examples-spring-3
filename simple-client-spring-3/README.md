# Example Market Data Client
Connects to FIX for market data and publishes it over websocket to a simple html UI.

## Configuration
Filter the markets you subscribe to by modifying filters in application.yml

```yml
  filter:
    # filters are additive
    # instruments not matching all the specified filter criteria are omitted
    # filter criteria may be left blank
    underlyingSymbols:
    products: # In this version of the application only the first product specified will be evaluated
      - 102 # EVENTS
    symbolRegularExpressions:
      - .*NFL-W8* # NFL 'week 8' event contracts
    periods: # I, D, W, M, O
    securitySubTypes:
```

## UI
visit localhost:8080 to see a collated list of markets with their latest top-of-book bid and offer shown.

Be aware, based on the filter configuration you provide, there may be a very large number of markets displayed.

You may want to provide a more specific filter configuration in order to receive a smaller subset of markets which can be rendered more easily in the web UI.