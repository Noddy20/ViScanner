[![Build Status](https://img.shields.io/badge/build-1.0-brightgreen.svg)](https://github.com/Noddy20/ViScanner/)

#ViScanner

ViScanner allows you to scan Url's and File Hashes for malware/viruses etc. using VirusTotal API.

##Gradle

```
implementation 'com.github.Noddy20:ViScanner:1.+'
```

##Sample App

Check out the [Sample App](https://github.com/Noddy20/ViScanner/app/)

##Usage

Add above dependency and sync your project, and get your VirusTotal API key.

###VirusTotal API KEY

ViScanner uses VirusTotal API, which requires an API key. To get the API key you must sign up to [VirusTotal Community](https://www.virustotal.com/#/join-us). Once you have a valid VirusTotal Community account you will find your personal API key in your personal settings section.


[![Build Status](https://img.shields.io/badge/-Important-red.svg)](https://developers.virustotal.com/reference)

The VirusTotal API must not be used in commercial products or services, it can not be used as a substitute for antivirus products and it can not be integrated in any project that may harm the antivirus industry directly or indirectly. Noncompliance of these terms will result in immediate permanent ban of the infractor individual or organization.

Under all circumstances VirusTotal's [Terms of Service](https://support.virustotal.com/hc/en-us/articles/115002145529-Terms-of-Service) and [Privacy Policy](https://support.virustotal.com/hc/en-us/articles/115002168385-Privacy-Policy) must be respected.

###Initialize

```
String apiKey = "Your_API_Key";
ViScanBuilder viScanBuilder = new ViScanBuilder(getApplicationContext(), apiKey);
```

###Callbacks

When a scan is completed for single item, all items and when a scan fails you can listen to these callbacks by ScanListener -

```
viScanBuilder.setScanListener(scanListener);

....

private ScanListener scanListener = new ScanListener() {

        @Override
        public void scanItemFailed(int cause, @Nullable Exception e) {
            Log.v(TAG, "Scan Failed "+cause+ " e "+e);
            //e might be null
        }

        @Override
        public void scanItemResult(int status, int totalAv, int positives, String jsonResult) {
            Log.v(TAG, "Scan Status "+status+" Total AV "+totalAv+ " Positives "+positives);
        }

        @Override
        public void scanFinalResult(List<String> jsonResults) {
            Log.v(TAG, "json Res "+jsonResults.size());
        }
    };
```   

scanItemFailed : e (Exception) might be null & cause can be -

FILE_NOT_FOUND = File not found on local device by path you provied,
INVALID_URL = Url you provided to scan is not valid,
REQUEST_LIMIT_EXCEEDED = Scan request limit exceeded (Visit [VirusTotal API Docs](https://developers.virustotal.com/reference) for more info),
BAD_REQUEST = Your request was somehow incorrect. This can be caused by arguments with wrong values,
INVALID_API_KEY = API key is not provided or invalid or You don't have enough privileges to make the request,
OTHER_REASON = like network connection error, permission error etc.

scanItemResult : provides result when scan for single item completes -
int status == 0 : scan result for url/file hash not availeble on VirusTotal
int status == 1 : scan result found
int status == 2 : the requested item is still queued for analysis

int totalAv : gives number of total antivirus tools scanned the item
int positives : gives number of total positive (url/file is malicious) scan results

String jsonResult : Complete scan result in Json format (Visit [VirusTotal API Docs](https://developers.virustotal.com/reference) for example)

scanFinalResult : result of all items scanned.


#License

Under all circumstances VirusTotal's [Terms of Service](https://support.virustotal.com/hc/en-us/articles/115002145529-Terms-of-Service) and [Privacy Policy](https://support.virustotal.com/hc/en-us/articles/115002168385-Privacy-Policy) must be respected.

