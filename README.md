# FlowrSpot

A demonstration app for fetching a list of flowers with a given search criterion.

Written in Kotlin in an MVVM architecture. 
Uses the new lifecycle components' paging library to support seamless paging with endless RecyclerView scrolling.

Uses Retrofit with Moshi for JSON parsing and a Coroutine call adapter factory to transform Retrofit calls into Kotlin coroutine Deferreds.

Since the REST API (currently) only has data for the first page, there are two build variants (besides the unsigned release variant):
- debug: uses a wrapper service that creates fake data for all requests on a set number of subsequent pages to enable pagination
- debug_realdata: directly calls the API without any data modification
