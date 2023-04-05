package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should return weather response"
    request {
        method GET()
        url("/api/v1/weather/ankara")
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body("""{
                            "cityName": "Ankara",
                            "country": "Turkey",
                            "temperature": 10.0,
                            "updatedTime": "2023-03-05 12:35"
                            }
             """)
    }
}
