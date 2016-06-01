var ngrok = require('ngrok');
var externalURL="";
var port = 8080;
ngrok.connect({
	proto: 'http',
	addr: port
}, function (err, url) {
	if(err) {
		console.log(err);
		return;
	}
	var tmp = url.split(":");
	externalURL="http:"+tmp[1]+"/";
	var fs = require('fs');
	fs.writeFile("/tmp/externalURL", externalURL, function(err) {
		if(err) { return console.log(err); }
	});

});
ngrok.connect(port);

