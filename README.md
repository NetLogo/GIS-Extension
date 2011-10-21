# NetLogo GIS extension

This package contains the NetLogo GIS extension.

## Using

This extension is bundled with NetLogo. For instructions on using it, or for more information about NetLogo extensions, see the NetLogo User Manual.

## Credits

The GIS extension was written by Eric Russell.

## Building

Use the NETLOGO environment variable to tell the Makefile which NetLogoLite.jar to compile against.  For example:

    NETLOGO=/Applications/NetLogo\\\ 5.0 make

If compilation succeeds, `gis.jar` will be created.

## Terms of Use

GIS Extension © 2001-2011 Eric Russell and Daniel Edelson. 
All rights reserved.

The Java source code (files in the src directory whose names end in ".java") and binary object code (the file "gis.jar") of the GIS Extension are made available to you subject to the terms of the following license:

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * The names of its contributors may not be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

<pre>

--------------------------------------------------------------------------------

The following data files included with the GIS Extension contain proprietary 
and confidential property of Environmental Systems Research Institute, Inc. 
(ESRI), Redlands, CA, and its licensor(s).  These files are:

  cities.dbf, cities.shp, cities.shx, cities.txt,
  countries.dbf, countries.shp, countries.shx, countries.xml
  rivers.dbf, rivers.shp, rivers.shp, rivers.txt
  
These files are made available under the following license:

Proprietary Rights and Copyright: Licensee acknowledges that the Data and 
Related Materials contain proprietary and confidential property of ESRI and 
its licensor(s). The Data and Related Materials are owned by ESRI and its 
licensor(s) and are protected by United States copyright laws and applicable 
international copyright treaties and/or conventions.

THE LICENSEE EXPRESSLY ACKNOWLEDGES THAT THE DATA CONTAIN 
SOME NONCONFORMITIES, DEFECTS, OR ERRORS. ESRI DOES NOT 
WARRANT THAT THE DATA WILL MEET LICENSEE'S NEEDS OR 
EXPECTATIONS, THAT THE USE OF THE DATA WILL BE UNINTERRUPTED, 
OR THAT ALL NONCONFORMITIES, DEFECTS, OR ERRORS CAN OR WILL 
BE CORRECTED. ESRI IS NOT INVITING RELIANCE ON THESE DATA, AND 
THE LICENSEE SHOULD ALWAYS VERIFY ACTUAL DATA.   

THE DATA AND RELATED MATERIALS CONTAINED THEREIN ARE PROVIDED 
"AS-IS," WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. 

IN NO EVENT SHALL ESRI AND/OR ITS LICENSOR(S) BE LIABLE FOR COSTS 
OF PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES, LOST PROFITS, 
LOST SALES OR BUSINESS EXPENDITURES, INVESTMENTS, OR 
COMMITMENTS IN CONNECTION WITH ANY BUSINESS, LOSS OF ANY 
GOODWILL, OR FOR ANY INDIRECT, SPECIAL,  INCIDENTAL, EXEMPLARY, OR 
CONSEQUENTIAL DAMAGES ARISING OUT OF THIS AGREEMENT OR USE OF 
THE DATA AND RELATED MATERIALS, HOWEVER CAUSED, ON ANY THEORY 
OF LIABILITY, AND WHETHER OR NOT ESRI AND/OR ITS LICENSOR(S) HAVE 
BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. THESE LIMITATIONS 
SHALL APPLY NOTWITHSTANDING ANY FAILURE OF ESSENTIAL PURPOSE 
OF ANY EXCLUSIVE REMEDY.

Third Party Beneficiary: ESRI's licensor(s) has (have) authorized ESRI to 
(sub)distribute and (sub)license their data as incorporated into the Data and 
Related Materials. As an intended third party beneficiary to this Agreement, 
the ESRI licensor(s) is (are) entitled to directly enforce, in its own name, 
the rights and obligations undertaken by the Licensee and to seek all legal 
and equitable remedies as are afforded to ESRI.

ESRI is a trademark of Environmental Systems Research Institute, Inc.

--------------------------------------------------------------------------------

The Java Advanced Imaging libraries (the binary files jai_codec-1.1.3.jar and
jai_core-1.1.3.jar) are copyright (c) 2006 Sun Microsystems, and are made 
available under the following license:

Sun Microsystems, Inc.
Binary Code License Agreement

JAVA ADVANCED IMAGING API, VERSION 1.1.3

READ THE TERMS OF THIS AGREEMENT AND ANY PROVIDED SUPPLEMENTAL LICENSE TERMS
(COLLECTIVELY "AGREEMENT") CAREFULLY BEFORE OPENING THE SOFTWARE MEDIA
PACKAGE.  BY OPENING THE SOFTWARE MEDIA PACKAGE, YOU AGREE TO THE TERMS OF
THIS AGREEMENT.  IF YOU ARE ACCESSING THE SOFTWARE ELECTRONICALLY, INDICATE
YOUR ACCEPTANCE OF THESE TERMS BY SELECTING THE "ACCEPT" BUTTON AT THE END OF
THIS AGREEMENT.  IF YOU DO NOT AGREE TO ALL THESE TERMS, PROMPTLY RETURN THE
UNUSED SOFTWARE TO YOUR PLACE OF PURCHASE FOR A REFUND OR, IF THE SOFTWARE IS
ACCESSED ELECTRONICALLY, SELECT THE "DECLINE" BUTTON AT THE END OF THIS
AGREEMENT. 

1.  LICENSE TO USE.  Sun grants you a non-exclusive and non-transferable
license for the internal use only of the accompanying software and
documentation and any error corrections provided by Sun (collectively
"Software"), by the number of users and the class of computer hardware for
which the corresponding fee has been paid. 

2.  RESTRICTIONS.  Software is confidential and copyrighted. Title to
Software and all associated intellectual property rights is retained by Sun
and/or its licensors.  Except as specifically authorized in any Supplemental
License Terms, you may not make copies of Software, other than a single copy
of Software for archival purposes.  Unless enforcement is prohibited by
applicable law, you may not modify, decompile, or reverse engineer Software. 
Licensee acknowledges that Software is not designed or intended for use in
the design, construction, operation or maintenance of any nuclear facility.
Sun Microsystems, Inc. disclaims any express or implied warranty of fitness
for such uses.   No right, title or interest in or to any trademark, service
mark, logo or trade name of Sun or its licensors is granted under this
Agreement. 

3.  LIMITED WARRANTY.  Sun warrants to you that for a period of ninety (90)
days from the date of purchase, as evidenced by a copy of the receipt, the
media on which Software is furnished (if any) will be free of defects in
materials and workmanship under normal use.  Except for the foregoing,
Software is provided "AS IS".  Your exclusive remedy and Sun's entire
liability under this limited warranty will be at Sun's option to replace
Software media or refund the fee paid for Software. 

4.  DISCLAIMER OF WARRANTY.  UNLESS SPECIFIED IN THIS AGREEMENT, ALL EXPRESS
OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED
WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
NON-INFRINGEMENT ARE DISCLAIMED, EXCEPT TO THE EXTENT THAT THESE DISCLAIMERS
ARE HELD TO BE LEGALLY INVALID. 

5.  LIMITATION OF LIABILITY.  TO THE EXTENT NOT PROHIBITED BY LAW, IN NO
EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR
DATA, OR FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
DAMAGES, HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.  In no event will Sun's liability
to you, whether in contract, tort (including negligence), or otherwise,
exceed the amount paid by you for Software under this Agreement.  The
foregoing limitations will apply even if the above stated warranty fails of
its essential purpose. 

6.  Termination.  This Agreement is effective until terminated.  You may
terminate this Agreement at any time by destroying all copies of Software. 
This Agreement will terminate immediately without notice from Sun if you fail
to comply with any provision of this Agreement.  Upon Termination, you must
destroy all copies of Software. 

7.  Export Regulations. All Software and technical data delivered under this
Agreement are subject to US export control laws and may be subject to export
or import regulations in other countries.  You agree to comply strictly with
all such laws and regulations and acknowledge that you have the
responsibility to obtain such licenses to export, re-export, or import as may
be required after delivery to you. 

8.  U.S. Government Restricted Rights.  If Software is being acquired by or
on behalf of the U.S. Government or by a U.S. Government prime contractor or
subcontractor (at any tier), then the Government's rights in Software and
accompanying documentation will be only as set forth in this Agreement; this
is in accordance with 48 CFR 227.7201 through 227.7202-4 (for Department of
Defense (DOD) acquisitions) and with 48 CFR 2.101 and 12.212 (for non-DOD
acquisitions). 

9.  Governing Law.  Any action related to this Agreement will be governed by
California law and controlling U.S. federal law.  No choice of law rules of
any jurisdiction will apply. 

10. Severability. If any provision of this Agreement is held to be
unenforceable, this Agreement will remain in effect with the provision
omitted, unless omission would frustrate the intent of the parties, in which
case this Agreement will immediately terminate. 

11. Integration.  This Agreement is the entire agreement between you and Sun
relating to its subject matter.  It supersedes all prior or contemporaneous
oral or written communications, proposals, representations and warranties and
prevails over any conflicting or additional terms of any quote, order,
acknowledgment, or other communication between the parties relating to its
subject matter during the term of this Agreement.  No modification of this
Agreement will be binding, unless in writing and signed by an authorized
representative of each party. 

                   JAVA ADVANCED IMAGING, VERSION 1.1.3
                        SUPPLEMENTAL LICENSE TERMS

These supplemental license terms ("Supplemental Terms") add to or modify the
terms of the Binary Code License Agreement (collectively, the "Agreement").
Capitalized terms not defined in these Supplemental Terms shall have the same
meanings ascribed to them in the Agreement. These Supplemental Terms shall
supersede any inconsistent or conflicting terms in the Agreement, or in any
license contained within the Software. 

1. Software Internal Use and Development License Grant.  Subject to the terms
and conditions of this Agreement, including, but not limited to Section 3
(Java Technology Restrictions) of these Supplemental Terms, Sun grants you a
non-exclusive, non-transferable, limited license to reproduce internally and
use internally the binary form of the Software, complete and unmodified, for
the sole purpose of designing, developing and testing your Java applets and
applications ("Programs"). 

2. License to Distribute Software.  In addition to the license granted in
Section 1 (Software Internal Use and Development License Grant) of these
Supplemental Terms, subject to the terms and conditions of this Agreement,
including but not limited to, Section 3 (Java Technology Restrictions) of
these Supplemental Terms, Sun grants you a non-exclusive, non-transferable,
limited license to reproduce and distribute the Software in binary code form
only, provided that you (i) distribute the Software complete and unmodified
and only bundled as part of your Programs, (ii) do not distribute additional
software intended to replace any component(s) of the Software, (iii) do not
remove or alter any proprietary legends or notices contained in the Software,
(iv) only distribute the Software subject to a license agreement that
protects Sun's interests consistent with the terms contained in this
Agreement, and (v) agree to defend and indemnify Sun and its licensors from
and against any damages, costs, liabilities, settlement amounts and/or
expenses (including attorneys' fees) incurred in connection with any claim,
lawsuit or action by any third party that arises or results from the use or
distribution of any and all Programs and/or Software. 

3. Java Technology Restrictions. You may not modify the Java Platform
Interface ("JPI", identified as classes contained within the "java" package
or any subpackages of the "java" package), by creating additional classes
within the JPI or otherwise causing the addition to or modification of the
classes in the JPI.  In the event that you create an additional class and
associated API(s) which (i) extends the functionality of the Java platform,
and (ii) is exposed to third party software developers for the purpose of
developing additional software which invokes such additional API, you must
promptly publish broadly an accurate specification for such API for free use
by all developers.  You may not create, or authorize your licensees to create
additional classes, interfaces, or subpackages that are in any way identified
as "java", "javax", "sun" or similar convention as specified by Sun in any
naming convention designation. 

4.  Java Runtime Availability.  Refer to the appropriate version of the Java
Runtime Environment binary code license (currently located at
http://www.java.sun.com/jdk/index.html) for the availability of runtime code
which may be distributed with Java applets and applications.

5. Trademarks and Logos. You acknowledge and agree as between you and Sun
that Sun owns the SUN, SOLARIS, JAVA, JINI, FORTE, and iPLANET trademarks and
all SUN, SOLARIS, JAVA, JINI, FORTE, and iPLANET-related trademarks, service
marks, logos and other brand designations ("Sun Marks"), and you agree to
comply with the Sun Trademark and Logo Usage Requirements currently located
at http://www.sun.com/policies/trademarks. Any use you make of the Sun Marks
inures to Sun's benefit. 

6. Source Code. Software may contain source code that is provided solely for
reference purposes pursuant to the terms of this Agreement.  Source code may
not be redistributed unless expressly provided for in this Agreement. 

7. Termination for Infringement.  Either party may terminate this Agreement
immediately should any Software become, or in either party's opinion be
likely to become, the subject of a claim of infringement of any intellectual
property right.

8. Third Party Code. Additional copyright notices and license terms
applicable to portions of the Software are set forth in the
THIRDPARTYLICENSEREADME. In addition to any terms and conditions of any third
party open source/freeware license identified in the THIRDPARTYLICENSEREADME,
the disclaimer of warranty and limitation of liability provisions in
paragraphs 5 and 6 of the Binary Code License Agreement shall apply to all
Software in this distribution.

For inquiries please contact: Sun Microsystems, Inc., 4150 Network Circle,
Santa Clara, California 95054, U.S.A

The source code of the Java Advanced Imaging library is available at:

  https://jai.dev.java.net/

--------------------------------------------------------------------------------
 
The JTS library (the binary file "jts-1.9.jar") is copyright (c) Vivid 
Solutions, and is made available under the following license:

                  GNU LESSER GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.


  This version of the GNU Lesser General Public License incorporates
the terms and conditions of version 3 of the GNU General Public
License, supplemented by the additional permissions listed below.

  0. Additional Definitions. 

  As used herein, "this License" refers to version 3 of the GNU Lesser
General Public License, and the "GNU GPL" refers to version 3 of the GNU
General Public License.

  "The Library" refers to a covered work governed by this License,
other than an Application or a Combined Work as defined below.

  An "Application" is any work that makes use of an interface provided
by the Library, but which is not otherwise based on the Library.
Defining a subclass of a class defined by the Library is deemed a mode
of using an interface provided by the Library.

  A "Combined Work" is a work produced by combining or linking an
Application with the Library.  The particular version of the Library
with which the Combined Work was made is also called the "Linked
Version".

  The "Minimal Corresponding Source" for a Combined Work means the
Corresponding Source for the Combined Work, excluding any source code
for portions of the Combined Work that, considered in isolation, are
based on the Application, and not on the Linked Version.

  The "Corresponding Application Code" for a Combined Work means the
object code and/or source code for the Application, including any data
and utility programs needed for reproducing the Combined Work from the
Application, but excluding the System Libraries of the Combined Work.

  1. Exception to Section 3 of the GNU GPL.

  You may convey a covered work under sections 3 and 4 of this License
without being bound by section 3 of the GNU GPL.

  2. Conveying Modified Versions.

  If you modify a copy of the Library, and, in your modifications, a
facility refers to a function or data to be supplied by an Application
that uses the facility (other than as an argument passed when the
facility is invoked), then you may convey a copy of the modified
version:

   a) under this License, provided that you make a good faith effort to
   ensure that, in the event an Application does not supply the
   function or data, the facility still operates, and performs
   whatever part of its purpose remains meaningful, or

   b) under the GNU GPL, with none of the additional permissions of
   this License applicable to that copy.

  3. Object Code Incorporating Material from Library Header Files.

  The object code form of an Application may incorporate material from
a header file that is part of the Library.  You may convey such object
code under terms of your choice, provided that, if the incorporated
material is not limited to numerical parameters, data structure
layouts and accessors, or small macros, inline functions and templates
(ten or fewer lines in length), you do both of the following:

   a) Give prominent notice with each copy of the object code that the
   Library is used in it and that the Library and its use are
   covered by this License.

   b) Accompany the object code with a copy of the GNU GPL and this license
   document.

  4. Combined Works.

  You may convey a Combined Work under terms of your choice that,
taken together, effectively do not restrict modification of the
portions of the Library contained in the Combined Work and reverse
engineering for debugging such modifications, if you also do each of
the following:

   a) Give prominent notice with each copy of the Combined Work that
   the Library is used in it and that the Library and its use are
   covered by this License.

   b) Accompany the Combined Work with a copy of the GNU GPL and this license
   document.

   c) For a Combined Work that displays copyright notices during
   execution, include the copyright notice for the Library among
   these notices, as well as a reference directing the user to the
   copies of the GNU GPL and this license document.

   d) Do one of the following:

       0) Convey the Minimal Corresponding Source under the terms of this
       License, and the Corresponding Application Code in a form
       suitable for, and under terms that permit, the user to
       recombine or relink the Application with a modified version of
       the Linked Version to produce a modified Combined Work, in the
       manner specified by section 6 of the GNU GPL for conveying
       Corresponding Source.

       1) Use a suitable shared library mechanism for linking with the
       Library.  A suitable mechanism is one that (a) uses at run time
       a copy of the Library already present on the user's computer
       system, and (b) will operate properly with a modified version
       of the Library that is interface-compatible with the Linked
       Version. 

   e) Provide Installation Information, but only if you would otherwise
   be required to provide such information under section 6 of the
   GNU GPL, and only to the extent that such information is
   necessary to install and execute a modified version of the
   Combined Work produced by recombining or relinking the
   Application with a modified version of the Linked Version. (If
   you use option 4d0, the Installation Information must accompany
   the Minimal Corresponding Source and Corresponding Application
   Code. If you use option 4d1, you must provide the Installation
   Information in the manner specified by section 6 of the GNU GPL
   for conveying Corresponding Source.)

  5. Combined Libraries.

  You may place library facilities that are a work based on the
Library side by side in a single library together with other library
facilities that are not Applications and are not covered by this
License, and convey such a combined library under terms of your
choice, if you do both of the following:

   a) Accompany the combined library with a copy of the same work based
   on the Library, uncombined with any other library facilities,
   conveyed under the terms of this License.

   b) Give prominent notice with the combined library that part of it
   is a work based on the Library, and explaining where to find the
   accompanying uncombined form of the same work.

  6. Revised Versions of the GNU Lesser General Public License.

  The Free Software Foundation may publish revised and/or new versions
of the GNU Lesser General Public License from time to time. Such new
versions will be similar in spirit to the present version, but may
differ in detail to address new problems or concerns.

  Each version is given a distinguishing version number. If the
Library as you received it specifies that a certain numbered version
of the GNU Lesser General Public License "or any later version"
applies to it, you have the option of following the terms and
conditions either of that published version or of any later version
published by the Free Software Foundation. If the Library as you
received it does not specify a version number of the GNU Lesser
General Public License, you may choose any version of the GNU Lesser
General Public License ever published by the Free Software Foundation.

  If the Library as you received it specifies that a proxy can decide
whether future versions of the GNU Lesser General Public License shall
apply, that proxy's public statement of acceptance of any version is
permanent authorization for you to choose that version for the
Library.

The source code of the JTS library is available at:

  http://sourceforge.net/projects/jts-topo-suite/

--------------------------------------------------------------------------------

The NGUnits library (the binary file ngunits-1.0.jar) is copyright (C) 2010-2011 
National Geographic Society and is made available under the following license:
    
    Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

        Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
        Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
        Neither the name of the National Geographic Society nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The source code of the NGUnits library is available at:

  https://github.com/erussell/ngunits

--------------------------------------------------------------------------------

The Apache Commons codec, httpclient, and logging libraries (the binary 
files commons-codec-1.3.jar, commons-httpclient-3.0.1.jar, and 
commons-logging-1.1.jar) are copyrighted by The Apache Software Foundation, 
and are made available under the following license:

                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION

   1. Definitions.

      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.

      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.

      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.

      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.

      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.

      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.

      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).

      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.

      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."

      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.

   2. Grant of Copyright License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.

   3. Grant of Patent License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.

   4. Redistribution. You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:

      (a) You must give any other recipients of the Work or
          Derivative Works a copy of this License; and

      (b) You must cause any modified files to carry prominent notices
          stating that You changed the files; and

      (c) You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and

      (d) If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.

      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.

   5. Submission of Contributions. Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.

   6. Trademarks. This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.

   7. Disclaimer of Warranty. Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.

   8. Limitation of Liability. In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.

   9. Accepting Warranty or Additional Liability. While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.

   END OF TERMS AND CONDITIONS

   APPENDIX: How to apply the Apache License to your work.

      To apply the Apache License to your work, attach the following
      boilerplate notice, with the fields enclosed by brackets "[]"
      replaced with your own identifying information. (Don't include
      the brackets!)  The text should be enclosed in the appropriate
      comment syntax for the file format. We also recommend that a
      file or class name and description of purpose be included on the
      same "printed page" as the copyright notice for easier
      identification within third-party archives.

   Copyright [yyyy] [name of copyright owner]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

The source code of the Commons libraries are available at:

  http://commons.apache.org/codec/,
  http://hc.apache.org/httpclient-3.x/, and
  http://commons.apache.org/logging/
</pre>
