Welcome to the Java Web Parts Cookbook!

The Java Web Parts Cookbook is a collection of "recipes", or ways that JWP can
be used.  This is something of a FAQ, but for each topic here you will find a
complete webapp to play with that demonstrates some area or areas of
Java Web Parts.  These are fully working applications of JWP that you can take
and use in your own work, or use as inspiration for your own creations.

The jwp_cookbook archive contains a bunch of ready-to-use webapps that you
can just drop in your app server of choice and fire up.  They are in exploded
form, not WAR form.  You should not need to configure anything in most
containers, just drop it in the appropriate place (for instance, /webapps if
using Tomcat) and give it a shot.

* jwpcb_tas -- Type-Ahead Suggestions With AjaxParts Taglib
  Made famous by Google, this is a working example of creating something like
  Google Suggests with the AjaxParts Taglib component of JWP.

* jwpcb_dds -- Dynamic Double Select With AjaxParts Taglib
  Another popular use of Ajax is the ability to dynamically populate a <select>
  element when a selection is made in another.  This demonstrates two ways of
  doing that with the AjaxParts Taglib component of JWP.

* jwpcb_karnak -- Karnak Type-Ahead Suggestions With AjaxParts Taglib
  This is close connected to the first app. Only this one has more make-up to it.
  This is a webapp from the book  "Practical Ajax Projects with Java
  Technology" by Frank W. Zammetti.

* jwpcb_rdr -- An Ajax RSS Reader With AjaxParts Taglib
  This is a webapp that could be useful too. It is an Ajax RSS Reader. Look 
  how you can use the timer and manual tags from APT.
  This is a webapp from the book  "Practical Ajax Projects with Java
  Technology" by Frank W. Zammetti.

* jwpcb_wwm -- Dynamically Adding Lines With AjaxParts Taglib (We Want More)
  (This example will be done using the ajax:manual tag and custom handlers).
  In this app you see how to add a line (or more) using a More button. The line 
  you add with APT also contains select boxes like in jwpcb_dds. 
  It shows you a way to deal with adding lines on a form by user request 
  (with the complication that these lines in their turn also contain APT calls).
  Inspired by pdaplyn (Pete).
