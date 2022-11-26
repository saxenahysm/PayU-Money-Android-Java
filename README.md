
### PayU-Money-Implementation for Android
----------
### Steps
- Hash Generation
  - Hash Generation Principle Must follow https://payumobile.gitbook.io/sdk-integration/hash-generation
  - Dynamic & Static Hash https://payumobile.gitbook.io/sdk-integration/android/payucheckoutpro/hash-details
  - Set up the payment hashes https://payumobile.gitbook.io/sdk-integration/android/payucheckoutpro/set-up-the-payment-hashes
  
    
  //payment_hash = sha512(key|txnid|amount|productinfo|firstname|email|||||||||||SALT)
  //strtolower(hash('sha512', $payhash_str));
  {hashString=gtKFFx|payment_hash|GET|, hashName=payment_hash}
  
  //payment_related_details_for_mobile_sdk
  {hashString=gtKFFx|payment_related_details_for_mobile_sdk|default|, hashName=payment_related_details_for_mobile_sdk}
  sha512(key|txpayment_related_details_for_mobile_sdknid|default|SALT)

  //get_sdk_configuration
  {hashString=gtKFFx|get_sdk_configuration|GET|, hashName=get_sdk_configuration}
  sha512(key|get_sdk_configuration|GET|SALT)
  
  //get_merchant_ibibo_codes
  {hashString=gtKFFx|get_merchant_ibibo_codes|default|, hashName=get_merchant_ibibo_codes}
  sha512(key|get_merchant_ibibo_codes|default|SALT)

  //getEmiAmountAccordingToInterest
  {hashString=gtKFFx|getEmiAmountAccordingToInterest|amount|, hashName=get_merchant_ibibo_codes}
  sha512(key|getEmiAmountAccordingToInterest|amount|SALT)

  //verify_payment
  {hashString=gtKFFx|verify_payment|txnid|, hashName=get_merchant_ibibo_codes}
  sha512(key|verify_payment|txnid|SALT)

  //verify_payment
  {hashString=gtKFFx|verify_payment|txnid|, hashName=get_merchant_ibibo_codes}
  sha512(key|verify_payment|txnid|SALT)


  And So on

  
  
- Payment API 
  - Mandatory Fields https://devguide.payu.in/api/payments/payment-api/
---------
- Youtube link https://www.youtube.com/watch?v=GvaoZC6jMIE

