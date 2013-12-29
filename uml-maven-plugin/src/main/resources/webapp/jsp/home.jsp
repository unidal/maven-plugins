<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="a" uri="/WEB-INF/app.tld"%>
<%@ taglib prefix="w" uri="http://www.unidal.org/web/core"%>
<jsp:useBean id="model" type="org.unidal.maven.plugin.uml.UmlViewModel" scope="request" />

<a:layout>

	<br>

	<form action="${model.webapp}/uml">
		<table>
			<tr>
				<td><textarea id="uml" name="uml" style="height: 480px; width: 320px">${w:htmlEncode(model.uml)}</textarea></td>
				<td width="10"></td>
				<td valign="top"><span id="svg">${model.svg}</span></td>
			</tr>
			<tr>
				<td colspan="3">

					<button class="btn btn-medium btn-primary" type="submit">Update</button>

				</td>
			</tr>
		</table>
	</form>

	<script lang="javascript">
	var interval = 500;
	var dirty = false;
	
	function refresh() {
		var uml = $("#uml").val();
		
		if (dirty) {
		   dirty = false;
		   $.ajax({
			  url: '${model.webapp}/uml',
			  type: 'POST',
			  data: 'type=text&uml=' + encodeURIComponent(uml),
			  async: false,
			  success: function(data) {
				// called when successful
				$('#svg').html(data);
			  },
			  error: function(e) {
				// called when there is an error
				// console.log(e.message);
			  }
			});
		}
		
		setTimeout(refresh, interval);
	}
	
	$(document).ready(function () {
		$('#uml').bind('input propertychange', function() {
		   dirty = true;
		});
		
	    $(function(){setTimeout(refresh, interval);});
	});
	</script>
</a:layout>
