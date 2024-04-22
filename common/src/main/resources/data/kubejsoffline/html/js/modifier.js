const MODIFIER = {
	PUBLIC: 1,
	PRIVATE: 2,
	PROTECTED: 4,
	STATIC: 8,
	FINAL: 16,
	SYNCHRONIZED: 32,
	VOLATILE: 64,
	TRANSIENT: 128,
	NATIVE: 256,
	INTERFACE: 512,
	ABSTRACT: 1024,
	STRICT: 2048,
	BRIDGE: 64,
	VARARGS: 128,
	SYNTHETIC: 4096,
	ANNOTATION: 8192,
	ENUM: 16384,
	MANDATED: 32768,
	CLASS_MODIFIERS: 3103,
	INTERFACE_MODIFIERS: 3087,
	CONSTRUCTOR_MODIFIERS: 7,
	METHOD_MODIFIERS: 3391,
	FIELD_MODIFIERS: 223,
	PARAMETER_MODIFIERS: 16,
	ACCESS_MODIFIERS: 7,

	isPublic(mod) {
		return (mod & 1) !== 0;
	},

	isPrivate(mod) {
		return (mod & 2) !== 0;
	},

	isProtected(mod) {
		return (mod & 4) !== 0;
	},

	isStatic(mod) {
		return (mod & 8) !== 0;
	},

	isFinal(mod) {
		return (mod & 16) !== 0;
	},

	isSynchronized(mod) {
		return (mod & 32) !== 0;
	},

	isVolatile(mod) {
		return (mod & 64) !== 0;
	},

	isTransient(mod) {
		return (mod & 128) !== 0;
	},

	isNative(mod) {
		return (mod & 256) !== 0;
	},

	isInterface(mod) {
		return (mod & 512) !== 0;
	},

	isAbstract(mod) {
		return (mod & 1024) !== 0;
	},

	isStrict(mod) {
		return (mod & 2048) !== 0;
	},

	toString(mod) {
		let sj = [];
		if (this.isPublic(mod)) {
			sj.push("public");
		}

		if (this.isProtected(mod)) {
			sj.push("protected");
		}

		if (this.isPrivate(mod)) {
			sj.push("private");
		}

		if (this.isAbstract(mod)) {
			sj.push("abstract");
		}

		if (this.isStatic(mod)) {
			sj.push("static");
		}

		if (this.isFinal(mod)) {
			sj.push("final");
		}

		if (this.isTransient(mod)) {
			sj.push("transient");
		}

		if (this.isVolatile(mod)) {
			sj.push("volatile");
		}

		if (this.isSynchronized(mod)) {
			sj.push("synchronized");
		}

		if (this.isNative(mod)) {
			sj.push("native");
		}

		if (this.isStrict(mod)) {
			sj.push("strictfp");
		}

		if (this.isInterface(mod)) {
			sj.push("interface");
		}

		return sj.join(' ');
	},

	isSynthetic(mod) {
		return (mod & 4096) !== 0;
	},

	isMandated(mod) {
		return (mod & '耀') !== 0;
	},

	classModifiers() {
		return 3103;
	},

	interfaceModifiers() {
		return 3087;
	},

	constructorModifiers() {
		return 7;
	},

	methodModifiers() {
		return 3391;
	},

	fieldModifiers() {
		return 223;
	},

	parameterModifiers() {
		return 16;
	}
}