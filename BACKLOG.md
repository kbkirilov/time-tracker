## New features suggestion

✅ #1 - <u>__Show last 10 projects with their id's when you go into Time tracking__</u> -> Edit Time Entry/Delete time entry.
  This will make accessing the right Time Entry much easier, without the need of going to the db table and checking
  the entry's id.<br>

✅ #2 - <u>__Time Budget Per Project__</u> - Create a new database table 'time_budgets' where I will enter the time given to me
by RS for each project. This can be used to check how much time left I have on a project. It could be used for notifications
in case I can put in more time for a project.<br>

✅ #3 <u>__Showing hours on edited entry too__</u> - At the moment when finish editing a Time Entry, the last step if to confirm
  the changes. This information is shown for the original entry and the updated entry:
  <br>*Original: Project name: DRE01119_SC03_REGENCY_GRAPHITE_III, Entry date: 2025-06-27, Start time: 11:00, End time: 11:30*
  <br>*Updated: Project name: DRE01119_SC03_REGENCY_GRAPHITE_III, Entry date: 2025-06-27, Start time: 11:00, End time: 12:30*
  <br>

I want to show the total hours per Time Entry on this confirmation step. Like this:

<br>*...Start time: 11:00, End time: 11:30, Hours: 0.5*
<br>*...Start time: 11:00, End time: 12:30, Hours: 1.5*
<br>

✅ #4 <u>__Show days worked for a particular project__</u> -> When I pull in a project report by project name, I need to also
  get the days I have worked on this project. So I need a third column 'days'.

✅ #5 <u>__Make 'today' the default when you enter a day for a time Entry__</u>

✅ #6 <u>__Make 'Enter start time' default value equals the 'end_time' of the last entry in database__</u> -> This way you don't have to
  go to the database sheet to look when did you finish the last task.

[ ] #7 <u>__Make 'Enter end time' default value equals the current time__</u> -> When you enter a new time entry the end time is usually what
  the current time is. So a default value for the current time would be very handy.

✅ #8 <u>__Allow entering 'cd1' on time entry project's stage __</u> -> Be able to just put it 'cd1' or 'pf' and automatically convert to uppercase. This
way I won't have to switch to Caps Lock every time.

[ ] #9 <u>__Implement progress bar for each project stage__</u> -> You can make that as an option to be shown in the detailed 
project analysis.

[ ] #10 <u>__Displaying total hours for project code__</u> -> Once the end of the month comes I have to enter the hours in workflowMax. There, I can't enter 
hours per set, but rather for the whole project. So for instance if I have worked on SC01 and SC02 for PJH01012, I need to sum the time for both SC01 and SC02
sets and enter it in WorkflowMax. Suggestion is that I make a new function that sums the hours per project code and displays them in the monthly report. 

## Fixes suggestions

~~<u>__Do not show a day in the reports if there are no working hours for it.__</u><br>~~
~~<u>__Provide support for "тодаъ"__</u><br>~~
<u>__If 'end_time' in a Time Entry finishes at 00:00 it makes it with a negative sign...__</u><br>