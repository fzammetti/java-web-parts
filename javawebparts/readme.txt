Java Web Parts
(c) 2005 Frank W. Zammetti, All Rights Reserved

Java Web Parts was created to be a repository for various small, reusable,
largely independant code of general interest to Java web application developers.
It contains a number of packages which can be used in a project in small
pieces.  In fact, each sub-package in the project is housed in its own JAR,
with little or no interdependency, and little external dependencies either.

All code in Java Web Parts was developed under J2SE 1.4.2.  It is currently
untested on any previous or newer versions.  I *EXPECT* it to work on any newer
version, but older versions could be a problem.  You can try it, but you
should be aware of this.  In addition, servlet spec 2.3 and JSP spec 1.2 are the
versions that are officially supported.  Others probably will work, but will not
be supported.

If you have any code you would like to contribute, or suggestions on what you 
would like to see, please feel free to contact us.  We welcome any and
all contributions!

Out intention is to roll out a number of smaller releases as quickly as
possible to encourage people to use the code and give feedback.

On the off chance that you haven't figure it out by now, Java Web Parts,
either bin or src distribution, is in the form of an exploded webapp.  Just
unzip it into your app server of choice (into webapps under Tomcat for instance)
and start'er up!

If you build Java Web Parts from source, the javadocs will be
generated locally as well.  There is an XSL file included in the src
directory for if you generate a build log with Ant.  The command line I
use to build is:

ant -DXmlLogger.file ./build_log.xml -listener org.apache.tools.ant.XmlLogger -v

Frank W. Zammetti
