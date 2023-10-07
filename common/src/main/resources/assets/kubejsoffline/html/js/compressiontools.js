function splitModifyCollect(input, separator, modifier) {
    let parts = input.split(separator);
    let output = [];
    for (let i = 0; i < parts.length; i++) {
        output.push(modifier(parts[i]));
    }
    return output.join(separator);
}

function compressFirstHalf(input, separator, modifier) {
    let lastIndexOf = input.lastIndexOf(separator);
    if (lastIndexOf === -1) {
        return modifier(input);
    }
    let start = modifier(input.substring(0, lastIndexOf));
    let end = modifier(input.substring(lastIndexOf + 1));
    return start + separator + end;
}

function mapUncompress(part) {
    return STRING_COMPRESSION_DATA[part];
}

function uncompressString(compressedString) {
    return compressedString;
    // return splitModifyCollect(
    // 		compressedString,
    // 		'<',
    // 		(a) => splitModifyCollect(
    // 				a,
    // 				'>',
    // 				(b) => splitModifyCollect(
    // 						b,
    // 						'$',
    // 						(c) => compressFirstHalf(
    // 								c,
    // 								'.',
    // 								mapUncompress
    // 						)
    // 				)
    // 		)
    // );
}