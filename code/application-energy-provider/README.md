# Energy provider
Provides electricity for consumer. Solar panels. 

## API Routes
127.0.0.1:8880/electricity-generation <br />
	- GET: Generated energy since last request. <br />
127.0.0.1:8880/electricity-generation/start <br />
	- POST: Starts generating electricity. <br />
127.0.0.1:8880/electricity-generation/start/{time} <br />
	- POST: Starts generating electricity, specifing start time. <br />
127.0.0.1:8880/electricity-generation/stop <br />
	- POST: Stops generating electricity. <br />
	
## Payload
electricity: electricity accumulated. <br />
type: electricity type (kW/h). <br />
days: total days of accumulating electricity. <br />
	
## Properties
Application system name=energy-provider <br />
Server address=127.0.0.1 <br />
Server port=8880 <br />
