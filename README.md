
# react-native-one-store-iap

## Getting started

`$ npm install react-native-one-store-iap --save`

### Mostly automatic installation

`$ react-native link react-native-one-store-iap`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNOneStoreIapPackage;` to the imports at the top of the file
  - Add `new RNOneStoreIapPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-one-store-iap'
  	project(':react-native-one-store-iap').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-one-store-iap/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-one-store-iap')
  	```


## Usage
```javascript
import RNOneStoreIap from 'react-native-one-store-iap';

// TODO: What to do with the module?
RNOneStoreIap;
```
  