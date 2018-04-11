# ITCHY Project
The ITCHY Project app is a collection of citizen science projects that anyone can partake in. More information can be found in [this video](https://www.youtube.com/watch?v=v-eHn1Zhlx4).

### Poison Ivy on the Appalachian Trail
This project allows the user to report sightings of poison ivy on the AT. Users can report one of three types of poison ivy and optionally upload pictures with it.

### More Projects Coming Soon
The ITCHY Project is new, more citizen science projects are on the way.

# Adding a Project to the App

### Add a Project Button
Add the following XML code to [fragment_home_page.xml](..blob/master/app/src/main/res/layout/fragment_home_page.xml) to create a button on the home page.
```xml
<Button  
  android:layout_width="match_parent"  
  android:layout_height="wrap_content"  
  android:layout_gravity="center_horizontal"  
  android:id="@+id/my_project_button"  
  android:text="My Project" />
```

### Link to Main Activity
Add the following code to [HomePageFragment.java](../blob/master/app/src/main/java/com/hci_capstone/poison_ivy_tracker/HomePageFragment.java) to send a new instance of your project fragment to the main activity.
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