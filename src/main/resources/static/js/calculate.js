function calculateAmount() {
	let rate = document.getElementById("rate").value;
	let quantity = document.getElementById("quantity").value;

	amount = rate * quantity;

	document.getElementById("amount").outerHTML = `<input type="text" class="form-control" id="amount" value="` + amount + `" placeholder="Amount">`;
}

function calculateTotalIncExp() {
	let amount = Number(document.getElementById("amount").value);
	let tax = Number(document.getElementById("tax").value);
	
	document.getElementById("totalAmount").value = amount + (amount * tax / 100);
}

$("#addStockInPartBtn").click(function () {
	productName = document.getElementById("productName");
	size = document.getElementById("size");
	colour = document.getElementById("colour");
	brand = document.getElementById("brand");
	quantity = document.getElementById("quantity");
	rate = document.getElementById("rate");
	amount = document.getElementById("amount");
	scanCode = document.getElementById("scanCode");

	var table = document.getElementById("productTable");
	var row = table.insertRow(2);

	row.insertCell(0).innerHTML = scanCode.value;
	row.insertCell(1).innerHTML = productName.value;
	row.insertCell(2).innerHTML = size.value;
	row.insertCell(3).innerHTML = colour.value;
	row.insertCell(4).innerHTML = brand.value;
	row.insertCell(5).innerHTML = quantity.value;
	row.insertCell(6).innerHTML = rate.value;
	row.insertCell(7).innerHTML = amount.value;

	var stockInParts =  `|~|` + productName.value + `|~|` + size.value + `|~|` + colour.value + `|~|` + brand.value + `|~|` + quantity.value + `|~|` + rate.value + `|~|` + amount.value + `|~|` + scanCode.value;
	row.insertCell(8).innerHTML = `<div class="dashboardicon2" style="text-align: left;" >`
			+ `<i class="fa fa-close buttonNew2" id="deleteProductBtn"></i>`
			+ `<input type="hidden" name="stockInParts" value="`+ stockInParts +`" >`;

	scanCode.value = ``;
	productName.value = ``;
	size.value = ``;
	colour.value = ``;
	brand.value = ``;
	quantity.value = ``;
	rate.value = ``;
	amount.value = ``;

});


//StockOut
$("#addStockOutPartBtn").click(function () {
	productId = document.getElementById("brands");
	quantity = document.getElementById("quantity");
	rate = document.getElementById("rate");
	amount = document.getElementById("amount");
	maxQuantity = document.getElementById("maxQuantity");
	
	if(Number(quantity.value) > Number(maxQuantity.value)) {
		alert('Entered quantity is greater than available quantity - ' + maxQuantity.value);
		return;
	}
	
	productName = document.getElementById("productNames");
	size = document.getElementById("sizes");
	colour = document.getElementById("colours");

	var brandSel = document.getElementById("brands");
	var brand= brandSel.options[brandSel.selectedIndex].text;

	var table = document.getElementById("productTable");
	var row = table.insertRow(1);

	row.insertCell(0).innerHTML = productName.value;
	row.insertCell(1).innerHTML = size.value;
	row.insertCell(2).innerHTML = colour.value;
	row.insertCell(3).innerHTML = brand;
	row.insertCell(4).innerHTML = quantity.value;
	row.insertCell(5).innerHTML = rate.value;
	row.insertCell(6).innerHTML = amount.value;

	var stockOutParts =  `|~|` + brandSel.value + `|~|` + quantity.value + `|~|` + rate.value + `|~|` + amount.value;
	
	row.insertCell(7).innerHTML = `<div class="dashboardicon2" style="text-align: left;" >`
			+ `<i class="fa fa-close buttonNew2" id="deleteProductBtn"></i>`
			+ `<input type="hidden" name="stockOutParts" value="`+ stockOutParts +`" >`;
	

});


$("#productTable").on('click', '#deleteProductBtn', function () {
	$(this).closest('tr').remove();
});











//Stock Out
$("#selectRoll").change(function (event) {
	var arr = event.target.value.split("~");

	id = arr[0];
	sortNo = arr[1];
	rollNo = arr[2];
	quantity = arr[3];
	rate = document.getElementById("rate").value;
	price = quantity * rate;

	var stockOutParts = id + `|~|` + price;

	length = $('#rollTable tr').length - 1;
	var table = document.getElementById("rollTable");
	var row = table.insertRow(length + 1);

	row.insertCell(0).innerHTML = length;
	row.insertCell(1).innerHTML = rollNo;
	row.insertCell(2).innerHTML = quantity;
	row.insertCell(3).innerHTML = price;
	row.insertCell(4).innerHTML = `<input type="button" id="deleteStockOutRollBtn" class="submit action-button2" value="Delete" >`
			+ `<input type="hidden" name="stockOutParts" class="submit action-button2" value="`+ stockOutParts +`" >`;

	totalQuantity = document.getElementById("totalQuantity");
	totalQuantity.value = Number(totalQuantity.value) + Number(quantity);

	totalPrice = document.getElementById("totalPrice");
	totalPrice.value = Number(totalPrice.value) + Number(price);

});


$("#rollTable").on('click', '#deleteStockOutRollBtn', function () {
	var currentRow=$(this).closest("tr"); 

	quantity = currentRow.find("td:eq(2)").text();
	price = currentRow.find("td:eq(3)").text();

	totalQuantity = document.getElementById("totalQuantity");
	totalQuantity.value = Number(totalQuantity.value) - Number(quantity);

	totalPrice = document.getElementById("totalPrice");
	totalPrice.value = Number(totalPrice.value) - Number(price);

	$(this).closest('tr').remove();

});

function populateTable(receivedBy) {
	var table = document.getElementById("rollTable"), i;

	if(receivedBy == 'KG') {
		table.rows[0].insertCell(3).outerHTML = "<th class='table_heading' id='bostaHeader'>Quantity(Bosta)</th>";
		table.rows[1].insertCell(3).outerHTML = "<td><input type='text' id='quantityBosta' class='quantity' placeholder='KG' ></td>";

		for (i = 2; i < table.rows.length; i++) {
			createCell(table.rows[i].insertCell(3), '', null);
		}
	} else if(receivedBy == 'M') {
		if(document.getElementById("bostaHeader") != null) {
			for (i = 0; i < table.rows.length; i++) {
				table.rows[i].deleteCell(3);
			}
		}
	}

}

function createCell(cell, text, style) {
	var div = document.createElement('div'); // create DIV element
	var txt = document.createTextNode(text); // create text node

	if(style != null) {
		div.setAttribute('class', style);        // set DIV class attribute
		div.setAttribute('className', style);    // set DIV class attribute for IE (?!)
	}

	div.appendChild(txt);                    // append text node to the DIV
	cell.appendChild(div);                   // append DIV to the table cell

}




function calculateAllAmounts(rate) {
	var elements = document.getElementsByClassName("quantity");
	//	for(var i = 0; i < elements.length; i++) {
	//		alert(`#` + elements[i].value);
	//	}

	var table = document.getElementById("stockInPartTable");
	for (var i = 0, row; row = table.rows[i]; i++) {
		//iterate through rows
		//rows would be accessed using the "row" variable assigned in the for loop

		alert(`Data is` + row.cells[2].innerHTML);

		for (var j = 0, col; col = row.cells[j]; j++) {
			//iterate through columns
			//columns would be accessed using the "col" variable assigned in the for loop
		}

	}

}

