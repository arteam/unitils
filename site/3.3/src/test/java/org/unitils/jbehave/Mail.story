!--START SNIPPET: jbehavestory
Scenario:  send correct email
Given send an email to  willemijn.wouters@unitils.be
Then check if the email is sent


Scenario: send correct email with correct header
Given send an email with willemijn.wouters@unitils.be with header just a testheader
Then check if the email has the correct header

!--END SNIPPET: jbehavestory