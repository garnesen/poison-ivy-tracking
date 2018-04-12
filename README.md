
# ITCHY Project
The ITCHY Project app is a collection of citizen science projects that anyone can partake in. More information can be found in [this video](https://www.youtube.com/watch?v=v-eHn1Zhlx4).

### Poison Ivy on the Appalachian Trail
This project allows the user to report sightings of poison ivy on the AT. Users can report one of three types of poison ivy and optionally upload pictures with it.

### More Projects Coming Soon
The ITCHY Project is new, more citizen science projects are on the way.

# Adding a Project to the App

### Add a Project Button
Add the following XML code to [fragment_home_page.xml](../master/app/src/main/res/layout/fragment_home_page.xml) to create a button on the home page.
```xml
<Button  
  android:layout_width="match_parent"  
  android:layout_height="wrap_content"  
  android:layout_gravity="center_horizontal"  
  android:id="@+id/my_project_button"  
  android:text="My Project" />
```

### Link to Main Activity
Add the following code to [HomePageFragment.java](../master/app/src/main/java/com/hci_capstone/poison_ivy_tracker/HomePageFragment.java) to send a new instance of your project fragment to the main activity.
```java
Button myProjectButton = view.findViewById(R.id.my_project_button);  
myProjectButton.setOnClickListener(new View.OnClickListener() {  
  @Override  
    public void onClick(View view) {  
      if (callback != null) {  
	    callback.onProjectSelected(new MyProjectFragment());  
      }  
    }  
});
```

### Create Your Project
Create a new package for all your project files and create ``MyProjectFragment.java``  that extends ``android.support.v4.app.Fragment`` . This will act as the "main activity" for your project page.

## Available Tools
### Instance ID
When the app is first run, an instance ID is created for the user. This ID can be accessed by calling ``InstanceID.getInstance().getId()``.

### Request Handler
You can queue a request by calling ``RequestHandler.getInstance().addToRequestQueue(...);``. An example can be seen in [SettingsSyncService.java](../master/app/src/main/java/com/hci_capstone/poison_ivy_tracker/SettingsSyncService.java).

### Location
The main activity of the app handles location services. To get access to this object, first add to your fragment the following code:
```java
private GetLocationListener locationCallback;

@Override
public void onAttach(Context context) {
    super.onAttach(context);

    try {
        locationCallback = (GetLocationListener) context;
    } catch (ClassCastException e) {
        throw new ClassCastException(context.toString() + " must implement GetLocationListener.");
    }

}
```
You can then get the location object like so:
```java
SimpleLocation location = locationCallback.getLocation();
double longitude = location.getLongitude();
double latitude = location.getLatitude();
double altitude = location.getAltitude();
```
## Adding Settings
First add your setting to [fragment_settings.xml](../master/app/src/main/res/xml/fragment_settings.xml). The following code is an example of what it may look like.
```xml
<EditTextPreference
  android:key="pref_my_setting"
  android:title="Setting Name"
  android:summary="This is my setting!" />
  ```
If you want the server to be alerted of the setting change, add your key to the ``SYNC_KEYS`` list in [SettingsFragment.java](../master/app/src/main/java/com/hci_capstone/poison_ivy_tracker/SettingsFragment.java). When the setting change occurs, a request will be made with the following body:
```json
{
	"uid": "a1a75dfe-b444-3512-d456-e7845h67895g6",
	"payloadType": "SETTINGS",
	"payload": {"pref_my_setting": "the new value"}
}
```