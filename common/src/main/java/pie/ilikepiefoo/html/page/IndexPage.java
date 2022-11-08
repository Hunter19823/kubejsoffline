package pie.ilikepiefoo.html.page;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.script.ScriptType;
import pie.ilikepiefoo.KubeJSOffline;
import pie.ilikepiefoo.html.tag.CustomTag;
import pie.ilikepiefoo.html.tag.collection.JSONDataTag;
import pie.ilikepiefoo.util.RelationType;
import pie.ilikepiefoo.util.json.ClassJSONManager;

import java.util.Map;
import java.util.Set;

public class IndexPage extends HTMLFile {
	private final Map<String, Map<Class<?>,Set<ScriptType>>> bindings;

	public IndexPage(Map<String, Map<Class<?>,Set<ScriptType>>> bindings, Gson gson) {
		this.bindings = bindings;
		HEADER_TAG.add(new CustomTag("style").setContent(CSS));
		HEADER_TAG.add(new JSONDataTag("DATA", ClassJSONManager.getInstance().getTypeData(), gson));
		JsonObject object = new JsonObject();
		for(Class<?> subject : KubeJSOffline.HELPER.getEventClasses()) {
			object.add(subject.getName(), ClassJSONManager.getInstance().findAllRelationsOf(subject, RelationType.SUPER_CLASS_OF));
		}
		HEADER_TAG.add(new JSONDataTag("EVENTS", object, gson));
		HEADER_TAG.add(new JSONDataTag("RELATIONS", RelationType.getRelationTypeData(), gson));
	}


	public static final String CSS = """
			/* A direct download of https://aurora.latvian.dev/style.css\s
			 All Credit goes to it's author LatvianModder.*//* https://github.com/lonekorean/gist-syntax-themes */
			@import url('https://fonts.googleapis.com/css?family=Open+Sans');

			html {
			    background-color: #201c21;
			}

			body {
			    margin: 20px;
			    color: #afa0ab;
			    font: 16px 'Open Sans', sans-serif;
			    font-family: Open Sans, Arial;
			    font-size: 16px;
			    margin: 2em auto;
			    max-width: 800px;
			    padding: 1em;
			    line-height: 1.4;
			    text-align: none;
			}

			a {
			    color: #4791b1;
			    text-decoration: none;
			}

			p {
			    margin: 0.2em 0;
			}

			a:visited {
			    color: #4791b1;
			}

			.type, .type:visited {
			    color: #c13479;
			}

			ul li img {
			    height: 1em;
			}

			ul,
			ol {
			    padding-left: 1em;
			}

			blockquote {
			    color: #456;
			    margin-left: 0;
			    margin-top: 2em;
			    margin-bottom: 2em;
			}

			blockquote span {
			    float: left;
			    margin-left: 1rem;
			    padding-top: 1rem;
			}

			blockquote author {
			    display: block;
			    clear: both;
			    font-size: .6em;
			    margin-left: 2.4rem;
			    font-style: oblique;
			}

			blockquote author:before {
			    content: "- ";
			    margin-right: 1em;
			}

			blockquote:before {
			    font-family: Times New Roman, Times, Arial;
			    color: #666;
			    content: open-quote;
			    font-size: 2.2em;
			    font-weight: 600;
			    float: left;
			    margin-top: 0;
			    margin-right: .2rem;
			    width: 1.2rem;
			}

			blockquote:after {
			    content: "";
			    display: block;
			    clear: both;
			}

			@media screen and (max-width:500px) {
			    body {
			        text-align: left;
			    }

			    div.fancyPositioning div.picture-left,
			    div.fancyPositioning div.tleft {
			        float: none;
			        width: inherit;
			    }

			    blockquote span {
			        width: 80%;
			    }

			    blockquote author {
			        padding-top: 1em;
			        width: 80%;
			        margin-left: 15%;
			    }

			    blockquote author:before {
			        content: "";
			        margin-right: inherit;
			    }
			}

			span.yes {
			    color: #26b36c;
			}

			span.no {
			    color: #ab495f;
			}

			span.other {
			    color: #42a3ff;
			}

			.error {
			    color: #ce3434;
			}

			table {
			    font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
			    border-collapse: collapse;
			    width: 100%;
			    text-align: left;
			}

			table td,
			table th {
			    border: 1px solid #1f1414;
			    padding: 8px;
			}

			table tr:nth-child(even) {
			    background-color: #00000024;
			}

			table tr:hover {
			    background-color: #424250;
			}

			table th {
			    padding-top: 12px;
			    padding-bottom: 12px;
			    text-align: left;
			    background-color: #383944;
			    color: #ddd;
			}

			pre {
			    background-color: #f2f2f2;
			    padding: 0.3em;
			    overflow-x: scroll;
			}

			img.tooltip, div.tooltip {
			    display: inline-block;
			}

			.tooltip .tooltiptext {
			    visibility: hidden;
			    max-width: 20em;
			    background-color: #2d2a2a;
			    color: #ead8da;
			    border-radius: 6px;
			    border: #101010 1px solid;
			    padding: 0.7em 1em;
			    position: absolute;
			    z-index: 1;
			}

			.tooltip:hover .tooltiptext {
			    visibility: visible;
			}

			img.icon {
			    height: 1em;
			    padding: 0;
			    margin: 0 0.3em 0 0;
			    display: inline-block;
			    vertical-align: middle;
			}

			.center-text {
			    text-align: center;
			}
			.logo {
			    height: 7em;
			}""";
}
