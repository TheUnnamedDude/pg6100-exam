Most features should be implemented except the merge patch for questions as
I couldn't manage to get it working. I used apache httpclient in the hystrix
because I couldn't manage to get the jax-rs http client to work. There is also
a issue with one SOAP test(for some reason wiremock returns the wrong result),
so its disabled, it worked on my desktop but not laptop and I didn't have the
time to fix it. Otherwise all features should be after spec and working.

The main web service should be available at http://server1.tud.pw:9090/quiz/
