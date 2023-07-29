# General Background
Stepper - a workflow\pipeline system that enables assembling different scenarios (called flows) from common components (called steps), including executing them and producting requried results.
The project includes different UI clients (simple CLI, Desktop, Client-server).
It includes component that manages users and permissions, serving multiple clients conccurrently and collecting information and statistics on their progress.
The system is developed in Java. It includes practice with multithread concerns, client server architecture, JSON serialization back and forth and much more
# Project's Structure
The application was developed in 3 parts:
1. Developed a console application where users could "talk" with our system by series of menus and available operations.
2. Developed a desktop application (via JavaFX architecture) which included a graphic UI on JavaFX components. Each interaction with user were based on different buttons and controllers.
3. Developed a web application (**this part is mainly covered in this readme, other parts won't be supported here**) which allowed users to dynamically register to system and suggested various operations - issue new stock/buy & sell stocks/deposit cash to system and more.
# On a Personal Note
This project was one of the first time I experienced working in client/server structure, developing a web application and implementing HTTP requests & responses. The project summarized all the subjects that were taught about java & web development. Working on a project from 0 was challenging yet very awarding especially when parts started to connect and form a complete system.
# Screenshots
**admin's application**  

Roles Management Screen:  
![image](https://github.com/ShahafS12/stepperProject/assets/104262302/86d391c5-12e4-4c2c-a4db-8694f3061733)  

User Management Screen:  
![image](https://github.com/ShahafS12/stepperProject/assets/104262302/c01cccdc-3b69-4fce-bd6a-46dc296f2083)  

History Screen:  
![history](https://github.com/ShahafS12/stepperProject/assets/104262302/4d9148d1-0e49-4a83-a8b5-a0ec558eb62d) 

Statistics screen:  
![statistics](https://github.com/ShahafS12/stepperProject/assets/104262302/b198e783-75f5-4e6d-a5a4-4c804b149e3f)  

**Client's application**  
Avilible Flows Screen:  
![image](https://github.com/ShahafS12/stepperProject/assets/104262302/d3ef21a8-df1a-4a0c-bd41-523be6830288)  

Flow Execution Screen:  
![image](https://github.com/ShahafS12/stepperProject/assets/104262302/8c33b5ad-00ba-487a-8e64-bc43fb2b9019)  

# Contributors
1.**Shahaf Shabo:**  
shahafsh12@gmail.com  
https://www.linkedin.com/in/shahaf-shabo-074326211/  
2.**Tomer Klein:**  
tomerkl22@gmail.com  
https://www.linkedin.com/in/tomer-klein-414136235/  







