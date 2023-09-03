function loadPackageName(id) {
	if (id <= -1) {
		console.log("Invalid id: " + id);
		return "unknown";
	}
	let names = [];
	let current = id;
	let currentData = PACKAGE_COMPRESSION_DATA[current];
	while (typeof (currentData) !== "string") {
		current = currentData[0];
		names.push(currentData[1]);
		currentData = PACKAGE_COMPRESSION_DATA[current];
	}
	names.push(currentData);
	return names.reverse().join(".");
}