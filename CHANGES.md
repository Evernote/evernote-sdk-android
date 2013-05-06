= 1.1.1 / 2013-5-6

* Updated to evernote-api 1.24
* Added sample code for search

= 1.1 / 2013-2-20

* Added automatic bootstrapping support for Yinxiang
* Added asynchronous wrappers of UserStore.Client and NoteStore.Client
* Added Factory method to generate appropriate AsyncClients
* Added Business and Linked AsyncNoteStore objects with helper methods
* Cleaned up architecture
* New sample code
* Bug fixes

= 1.0.2 / 2012-12-30

* Added android annotations
* Fixed readme formatting

= 1.0.1 / 2012-12-13

* Moved to Maven Central
* Updated dependency references to com.evernote.evernote-api which replaces lib-thrift and en-thrift
* Bug fixes

= 1.0 / 2012-11-01

* Migrated to Library Project
* Rearchitected most of EvernoteSession to reduce code
* Removed duplicate class libraries
* Added Maven support
* Moved All network requests to background threads
* Updated UI styles to Holo
* Created oauth activity to handle with webview
* Added functionality to HelloEdam to select pictures to upload
* Removed HelloEvernote, will be moving to another project
* Bug fixes

= 0.2.3 / 2012-09-28

* Include YinxiangApi.java in src.
* Remove use of String.isEmpty() to maintain compatibility with older SDK versions.

= 0.2.2 / 2012-09-19

* Adds support for the Evernote China API (Yinxiang Biji)

= 0.2.1 / 2012-09-08

* Set targetSdkVersion to 9
* Add known issues to README

= 0.2 / 2012-09-07

* Persist authentication token and related settings in EvernoteSession so that the
  user does not have to reauthenticate until their OAuth token expires or is
  revoked.

= Unversioned

* Initial release.
