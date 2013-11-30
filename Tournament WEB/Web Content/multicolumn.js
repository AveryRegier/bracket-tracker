/*
	MultiColumn text reflower v1.4 by Randy Simons
	
	Basic Usage:		
		new MultiColumn(document.getElementById("container"),new MultiColumnSettings);
				
		The "container" should be a div, with a single div inside. The content should be in the
		latter div. Use sematically correct HTML, thus use <h1>, <p>, <ul> etc.
		
		Values in the class MultiColumnSettings can be overridden. See the code below for the
		possible settings.
		
		You should invoke this script atfer the page has been rendered, so place the invokation at
		the bottom of the page, or (often better), add an onload-event.
		
		Some CSS is required. See the example below.
			
		USE VALID (X)HTML!
		
	Example:
		<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
		<html xmlns="http://www.w3.org/1999/xhtml">			
			<head>
				<title>MultiColumn example</title>
				<style type="text/css">
					/@Initial definitions for base column. 
					   Define the (minimum) width here, and optionally a padding @/
					.columnized div {
						float: left;
						width: 18em;		/@ When using a fixed number of columns, you can omit the width. Otherwise it must be set! This will be the *minimum* width of a column@/
						padding: 10px;		/@ You may use a padding... but thanks to IE you can only use pixels! @/
						position: relative; 		/@ Needed when using a 'read-on'-text @/
						text-align: justify;
						margin: 0;	 		/@ Don't use a margin! @/
					}
					
					/@ Optional 'read on'-message. Not used in this example. @/
					.columnized div .readOn {
						position: absolute;
						right: 1em;
						bottom: -0.5em;
						color: #999999;
					}
				</style>
				<script type="text/javascript" src="MultiColumn.js"></script>
				<script type="text/javascript">	
					//Minimalistic settings. You can tweak the settings by re-assigning the defaults in MultiColumnSettings.
					multiColumnSettings=new MultiColumnSettings;
					multiColumnSettings.classNameScreen='columnized';					
					
					window.onload = function () {
						new MultiColumn(document.getElementById("container"),multiColumnSettings);
					}
				</script>
			</head>
			<body>
				<div id="container">
					<div>
						  <h1>Heading</h1>
						  <p>text text text text</p>
						  <p>text text text text</p>	
					</div>
				</div>
			</body>
		</html>

	License:
		This file is entirely BSD-style licensed.
	
	More information and contact:
		http://randysimons.com

	History:
		v1.4 (1-6-2007)
			-Some code cleaning.
			-Improved the example.
			-Added/fixed heading-wrapping. A lonely heading a the bottom of a column is moved to the
			 next column.
		
		v1.3 (27-5-2006)
			- Added numberOfColumns setting. Now you can specify a fixed number of columns instead
			  of a calculation based on minimum width.
			- Improved printing options. A classNamePrint can be specified. If set, the original
			  column container is duplicated, inserted above the original container and the given
			  classname will be set. Use media-rules to diffentiate between print and screen-styles.
			- The old className is renamed to classNameScreen. It now sets the class name of the
			  *container*, instead of the columns"
			- The "read on" text can be specified.
			- Fixed IE6 compatibility, was broken since v1.2.1. (keyword: hasLayout. *sigh* IE6 is
			  simply obsolete.)
			- Automatically adds a clearing element at the bottom of each container.
			  
		v1.2.1 (18-3-2006)
			- Width of the container is used for calculations, instead of width of parent of
			  container.
			- Fix for resizing with more than one container.
			
		v1.2   (12-3-2006)
			- Settings are passed by creating an instance of the MultiColumnSettings class, so
			  you can easy change some default values at choice.
			- Added minSplitHeight. Only one column will be used if the height of this column is
			 less than minHeight.
			- Added minHeight. This is the minimum height for every column. Avoids 'flat' columns.
			- Added addReadOn. If true, the 'Read On' notice will be added to the columns.
			- BUG fixes in ParapgraphWrapper.
			- Added HeadingWrapper, so headings are kept with next paragraph.
				
		v1.1   (10-3-2006)
			- The height of the content in the columns is now equalized, by splitting elements and
			  wrapping their content to the next column. Currently only paragraphs and lists can
			  be splitted.
			- Introduced the ParapgraphWrapper and ListWrapper for this feature.
			- Added uncertainty. And a parameter to compensatate.
	
		v1.0   (8-3-2006)
			First release
*/

//Some stuff needs to be maintained in a global variable.
MultiColumnResizeTimer=null;
MultiColumnList=null;

function MultiColumnSettings() {
	this.extraHeight=50;		//Add extra height to a column. Increase this if the last column 'sticks out' too much.
	this.minSplitHeight=0;		//If the base column is smaller than minSplitHeight, the column does not split.
	this.minHeight=0;			//Minimum height of a column
	this.readOnText=null;		//Add the "read on" notice a the bottom of each column
	this.classNameScreen=null;	//Set the classname of *container* of the rendered columns for screen-media. Use to differentiate between JS/non-JS base column widths.
	this.classNamePrint=null;	//Set the classname of *container* of the rendered columns for print-media. (optional, may be null)
	this.numberOfColumns=null; 	//null: calculate number of columns based on minimum width. Number >0: use specified number of columns, always adapt width.
}

function MultiColumn(columnContainerIn,settingsIn) {
	//IE6 doesn't support HTMLElement prototyping. IE7 probably won't too. Let's aim for IE8! *sigh*	
	//But thank you, www.quirksmode.org!
	this.getStyle= function (element,stylePropW3,stylePropIE) {
		var y = null;
		if (element.currentStyle)
			y = element.currentStyle[stylePropIE];
		else if (window.getComputedStyle && document.defaultView.getComputedStyle(element,null)) {
			y = document.defaultView.getComputedStyle(element,null).getPropertyValue(stylePropW3);
		}
		return y;
	}
		
	this.generateColumns= function () {
		var i=0;
		var numColumns;
		
		//Obtain the base column. This column contains the original text.
		var baseColumn=this.columnContainer.getElementsByTagName('div').item(0);
		
		//Add a node with style: "clear: both;" to stretch the container-node.
		var clearingNode=document.createElement('span');
		clearingNode.style.display="block";
		clearingNode.style.clear="both";
		clearingNode.style.zoom="1"; //yet another work-around for a certain obsolete browser.
		this.columnContainer.appendChild(clearingNode);
		
		//Use specified number of columns, or calculate number based on minimum width?
		if (this.settings.numberOfColumns!=null) {
			//Use specified number of columns
			numColumns=this.settings.numberOfColumns;
		}	else {
			//Calculate the number of columns that can be added, based on width.
			numColumns=Math.floor(this.columnContainer.offsetWidth/(baseColumn.offsetWidth)); //baseColumn.getStyle('width') gives wrong value in Opera
		}
		
		//Calculate the available width for one column.
		var availableWidth=Math.floor((this.columnContainer.offsetWidth-10)/numColumns)-parseInt(this.getStyle(baseColumn,'padding-right','paddingRight'))-parseInt(this.getStyle(baseColumn,'padding-left','paddingLeft'));
		
		//Add new columns
		for (i=1;i<numColumns;i++) {
			this.columnContainer.insertBefore(baseColumn.cloneNode(false),this.columnContainer.firstChild);
		}
				
		//First, set the new width for the existing column..
		baseColumn.style.width=availableWidth+'px';
		
		//Get all columns in the container
		var columns=this.columnContainer.getElementsByTagName('div');
		
		//..then calculate the average needed height for other .
		var minHeight;
		
		if (baseColumn.offsetHeight<=this.settings.minSplitHeight) {
			var minHeight=baseColumn.offsetHeight;
		} else {
			var minHeight=Math.max(parseInt((baseColumn.offsetHeight+numColumns*this.settings.extraHeight)/columns.length),this.settings.minHeight);
		}

		//Cut/paste blocks from the baseColumn to the new columns, until the reached the minHeight.
		for (i=0;i<columns.length-1;i++) {
			var currentColumn=columns.item(i);
			currentColumn.style.width=availableWidth+'px';
			
			//Cut/paste blocks from the baseColumn to the current column, while the
			//current column has not reach the minHeight
			while (currentColumn.offsetHeight<minHeight && baseColumn.hasChildNodes()) {
				if (baseColumn.firstChild.nodeType==1) { //Node.ELEMENT_NODE Doesn't work in ^%@$#@$!!! IE6
					currentColumn.appendChild(baseColumn.firstChild);
				} else {
					baseColumn.removeChild(baseColumn.firstChild);
				}
			}
			
			//Some elements can be split and wrapped to the next column
			
			var lastChild=currentColumn.lastChild;
			var nextColumn=columns.item(i+1);
			switch (lastChild.nodeName.toLowerCase()) {
				case 'p': 
					new ParapgraphWrapper(currentColumn,lastChild,nextColumn,minHeight);
					break;
				case 'ul':
				case 'ol':
					new ListWrapper(currentColumn,lastChild,nextColumn,minHeight);					
					break;				
				default:
					//don't know what to do with this element. Let it stick out.
			}
			
			//Move headings at the bottom to next column. (this implies a proper usage of headings!)
			new HeadingWrapper(currentColumn,nextColumn);

			//add the 'read on' text.
			if (this.settings.readOnText!=null) {
				currentColumn.appendChild(this.readOnNode.cloneNode(true));
			}
		}
			
		//Stretch all columns to equal height
		var maxHeight=0;
		
		for (i=0;i<columns.length;i++) {
			maxHeight=Math.max(maxHeight,columns.item(i).offsetHeight);
		}			
		for (i=0;i<columns.length;i++) {
			columns.item(i).style.height=maxHeight+"px";
		}
	}
	
	//Initialisation starts here
	this.settings=settingsIn;
	
	if (this.settings.readOnText!=null) {					
		this.readOnNode=document.createElement('p');
		this.readOnNode.className="readOn";
		this.readOnNode.appendChild(document.createTextNode(this.settings.readOnText));
	}
	
	this.columnContainer=columnContainerIn;
	
	//If a screen class name is set, 
	if (this.settings.classNameScreen!=null) {
		//assign the classname.
		this.columnContainer.className=this.settings.classNameScreen;
	}
	
	//Store a copy of the original column	
	this.originalContent=columnContainerIn.cloneNode(true);
		
	//If a print class name is set, 
	if (this.settings.classNamePrint!=null) {
		//make a copy of the original node,
		var printNode=this.originalContent.cloneNode(true);
		//assign the classname
		printNode.className=this.settings.classNamePrint;
		//and insert it into the dom.
		this.columnContainer.parentNode.insertBefore(printNode,this.columnContainer);
	}	
	
	//Add this MultiColumn to the listener.
	if (MultiColumnList === null) {		
		MultiColumnList = new Array;
		if (window.addEventListener) {
			window.addEventListener('resize',multiColumnSetResizeTimer,false);		
		} else {
			window.attachEvent('onresize',multiColumnSetResizeTimer);
		}
	}
	
	MultiColumnList.push(this);
	
	//And do the magic!
	this.generateColumns();
}

//Regenerates the columns after a short delay after the user stopped resizing.
function multiColumnSetResizeTimer() {
	if (MultiColumnResizeTimer) {
		clearTimeout(MultiColumnResizeTimer);
	}
	MultiColumnResizeTimer=setTimeout(multiColumnResize,100);	
}
	
//Called when window is resized.
function multiColumnResize() {
	if (!window.addEventListener && window.attachEvent) { //Damned IE keeps fireing events when reflowing the text!
		window.detachEvent('onresize',multiColumnSetResizeTimer);
	}
	for (var i=0; i<MultiColumnList.length;i++) {
		var object = MultiColumnList[i];
		
		//Restore original situation
		var newCopy=object.originalContent.cloneNode(true);
		object.columnContainer.parentNode.replaceChild(newCopy,object.columnContainer);
		object.columnContainer=newCopy;
		
		//Regenerate columns
		object.generateColumns();
	}
	
	if (!window.addEventListener && window.attachEvent) { 
		setTimeout("window.attachEvent('onresize',multiColumnSetResizeTimer)",0);
	}
}

function ParapgraphWrapper(sourceColumnIn, sourceParagraphIn, destinationColumnIn, heightIn) {
	this.sourceColumn=sourceColumnIn;
	this.height=heightIn;
	
	/**
	* Recursively loops over given <source>, moving text from <source> to <dest>
	* until the column height is less or equal to the target height.
	*
	* Preconditions: <source> and <dest> are element nodes.
	*/
	this.processElement=function (source,dest) {
		var lastSourceChild;
		
		while (lastSourceChild=source.lastChild) {
			if (lastSourceChild.nodeType==1) {
				//Make a shalow clone copy of this element to the destination
				//node, to preserve styles and attributes.
				var newDest=lastSourceChild.cloneNode(false);
				dest.insertBefore(newDest,dest.firstChild);				
				//recursively process this node.
				if (this.processElement(lastSourceChild,newDest)) {
					return true;
				}							
			} else if (lastSourceChild.nodeType==3) {
				//Wrap this text node..
				if (this.wrapTextNode(lastSourceChild,dest)) {
					//..and return when the target has been reached.
					return true;
				}
			}
			
			//This node has been cleaned out. Remove it.
			source.removeChild(lastSourceChild);
		} 
				
		return false;
	}
	
	/**
	* Cuts words at the end of <source>, until the column height
	* is less or equal to target-height.
	* Cut words are then placed into <dest>
	*
	* Preconditions: <source> is a text node. <dest> is an element node.
	*/
	this.wrapTextNode=function (source,dest) {	
		var sourceText=source.nodeValue;
		
		//Split the text at spaces.
		var sourceTextAray=sourceText.split(/\s/);
		var destTextArray=new Array();
		
		//Keep removing words form the source until the column fits.
		while (this.sourceColumn.offsetHeight>this.height && sourceTextAray.length>0) {
			destTextArray.push(sourceTextAray.pop());
			source.nodeValue=sourceTextAray.join(' ');
		}
		
		//Add spaces at the front and end, if there are spaces in the original.
		var newText=(/^\s/.test(sourceText)?' ':'') + (destTextArray.reverse().join(' ')) + (/\s$/.test(sourceText)?' ':'');
		
		//Put the text into the destination node in the next column.
		dest.insertBefore(document.createTextNode(newText),dest.firstChild);		
		
		//return true if the target has been reached.
		return this.sourceColumn.offsetHeight<=this.height;
	}

	//Duplicate the current paragraph by shallow copy
	destinationColumnIn.insertBefore(sourceParagraphIn.cloneNode(false),destinationColumnIn.firstChild);	
	
	this.processElement(sourceParagraphIn,destinationColumnIn.firstChild);
	
	//check if the origal paragraph is emtpy
	if (sourceParagraphIn.offsetHeight==0) { //Check to see if normalized text is "" would be better..
		//Yes it's empty. Remove the empty paragraph.
		this.sourceColumn.removeChild(sourceParagraphIn);		
	}
}

function ListWrapper(sourceColumnIn, sourceListIn, destinationColumnIn, heightIn) {
	//Duplicate the current paragraph by shallow copy
	var newList=sourceListIn.cloneNode(false);
	
	destinationColumnIn.insertBefore(newList,destinationColumnIn.firstChild);
		
	//Loop over all elements in this list.
	while (currentElement=sourceListIn.lastChild) {
		if (sourceColumnIn.offsetHeight<=heightIn) {
			break;
		}
		
		if (currentElement.nodeName.toLowerCase()=='li') {
			newList.insertBefore(currentElement,newList.firstChild);			
		} else {		
			//remove the last element.
			sourceListIn.removeChild(currentElement);
		}
	}
		
	//Count current number of items.
	var numItems=1;
	var elementList=sourceListIn.childNodes;
	//count remaining items.
	for (var i=0;i<elementList.length;i++) {
		if (elementList[i].nodeName.toLowerCase()=='li') {
			numItems++;
		}
	}
	
	newList.start=numItems;
	
	//check if the origal list is emtpy
	if (sourceListIn.offsetHeight==0) { //Check to see if normalized text is "" would be better..
		//Yes it's empty. Remove the empty list.
		sourceColumnIn.removeChild(sourceListIn);		
	}
}

function HeadingWrapper(currentColumn,nextColumn) {
	//Wrap a heading, if there was one. 
	if (/^h[1-6]$/i.test(currentColumn.lastChild.nodeName)) {			
		nextColumn.insertBefore(currentColumn.lastChild,nextColumn.firstChild);	
	}
}