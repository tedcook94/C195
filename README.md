# C195
A scheduling application built with JavaFX for my C195 - Software II course at WGU. Uses a mySQL database to store users, customer records and appointments. Database ERD can be seen [here](https://drive.google.com/open?id=1MJw70lBYo2tGCYI0l4q9CggS6vsxtTf1).

## Functionalities
Overall:
*Supports adding, modifying and removing customers and appointments. 
*Generates notifications based on upcoming appointments and can create several different reports.
*Application is fully localized in English, Spanish and French.
*Automatically adjusts dates and times for system's location.

## Log-In:
*Users must log-in to access the main screen and other application functions
*Users cannot be added or edited from within the application. Contact your database administrator for assistance with users.
*Current users are as follows:
 -User: 'Ted'		Password: 'ted'
 -User: 'Kaylyn'	Password: 'kaylyn'
 -User: 'Toby'	Password: 'toby'
*Error messages pertaining to log-in are displayed on the log-in window.
*Upon log-in, a notification window will be created for all appointments beginning in the next 15 minutes that are associated with the user who just logged-in.
*Log-ins are recorded to the UserLog.txt file in the root of the project folder.
 - Each log-in is recorded with the user and timestamp of the log-in.
 - New records are appended to the end of the existing file.

## Calendar:
*Calendar opens in a monthly view on the current date.
*Large blue numbers in day panes indicate the number of appointments scheduled for that date.
*Calendar can be swapped between monthly and weekly views using the Switch to Monthly/Weekly View button.
*The month or week can be moved forward or back using the buttons next to the date at the top of the calendar.
*The calendar can jump to today's date using the Go to Current Date button.

## Customers:
*Active customers can be viewed in the table on the main screen.
*Customers can be added via the Add Customer window.
 - The Add Customer window is opened with the Add Customer button on the main screen.
 - Customer information is validated upon submission.
 - Customer information that matches an active customer already in the database will be rejected.
 - Customer information that matches an inactive customer already in the database will prompt an option to set that customer to active.
*Customers can be modified via the Modify Customer window.
 - The Modify Customer window is opened by selecting a customer from the table and then using the Modify Customer button on the main screen.
*Customers can be removed using the Remove Customer button.
 - A customer must be selected from the table to be removed.
 - Removing a customer keeps their entry in the database while setting them to inactive. This will also hide them from the table.

## Appointments:
*Upcoming scheduled appointments can be viewed in the table in the Appointment Summary window.
 - The Appointment Summary window is opened with the Appointment Summary button on the main screen.
 - More information about each appointment can be accessed by selecting an appointment from the table and then using the Get More Info button in the Appointment Summary window.
*Appointments can be created via the Add Appointment window.
 - The Add Appointment window is opened with the Add Appointment button on the main screen.
 - Appointment information is validated upon submission.
 - Appointment times are checked to not be outside business hours (9AM-5PM) and not to overlap with any other appointments.
*Appointments can be modified via the Modify Appointment window.
 - The Modify Appointment windows is opened by selecting a customer from the table in the Appointment Summary and then using the Modify button.
*Appointments can be deleted via the Delete button in the Appointment Summary window.
 - An appointment must be selected from the table to be deleted.
 - Deleting an appointment also deletes the appointment entry from the database.
	
## Reports:
*Reports can be generated from the Reports window.
 - The Reports window is opened with the Reports button on the main screen.
*Reports that can be generated include the following (all files are saved to project's root folder):
 - Number of Appointment Types by Month
   - Filename: AppointmentTypeByMonth.txt
 - Upcoming Schedule for Each Consultant
   - Filename: ScheduleByConsultant.txt
   - createdBy field in Appointment is used for consultant
 - Upcoming Meetings for Each Customer
   - Filename: ScheduleByCustomer.txt
   
## Acknowledgments
* @SirGoose3432 and his [javafx-calendar](https://github.com/SirGoose3432/javafx-calendar) project for inspiration on the monthly and weekly calendar views
* [Google Translate](https://translate.google.com/) for assistance in localization