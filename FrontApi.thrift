namespace java com.github.ddth.frontapi.internal.thrift

struct TApiResult {
    1: i32 status,
    2: string jsonOutput
}

service TApi {
 	TApiResult callApi(1: string authKey, 2: string moduleName, 3: string apiName, 4: string jsonInput)
}
