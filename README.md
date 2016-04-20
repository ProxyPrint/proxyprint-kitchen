# proxyprint-kitchen
ProxyPrint's back-end.

<h3>scripts</h3>
<p>Under the folder <b>scripts</b>, one may find <b>ppdb</b>, this script will allow us to do a fast local set-up test environment for our postgres database.</p>
<p>To see the usage, just type on the terminal ppdb to see the script options.<p>
<pre>
ppdb [options]:
	dac - drop and create database
	populate - populate proxyprint database
	users - select * from all users tables
</pre>
<p>If you want to use ppdb in every place like an usual command line tool, just copy the file <b>scripts/ppdb</b> to the <b>~/bin</b> on yourz file siztem.</p>
