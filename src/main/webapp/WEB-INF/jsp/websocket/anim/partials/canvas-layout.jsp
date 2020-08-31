<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<div style="display: block" id="show-game-wapper"><button id="btn-show-game" class="btn btn-primary btn-sm">Show Game</button></div>
<table id="game-layout" style="display: none" class="table-fixed">
	<tr>
		<td colspan="3"><button id="btn-hide-game" class="btn btn-secondary btn-sm">Hide Game</button></td>
	</tr>
	<tr>
		<td colspan="3"><span class="badge badge-secondary">Health</span>
			<div id="life-bar-wrapper" class="progress">
				<div id="life-bar" class="progress-bar progress-bar-striped"></div>
			</div></td>
		<td></td>
	</tr>
	<tr valign="top">
		<td colspan="3" style="width: ${winW}px">
			<div id="canvas-bg"
				style="background-repeat: no-repeat">
				<canvas id="tutorial" width="${winW}" height="${winH}"> </canvas>
			</div>
		</td>

		<td style="width: 300px">
			<p id="circuit-info"></p>
			<p id="entity-info"></p>
			<p id="realtime-info"></p>
			<p id="msg-info"></p>
		</td>
	</tr>
</table>
<script>
	function initCanvasEvent(){
		_byId("canvas-bg").style.backgroundImage = "url('<c:url value="/res/img/layout1.png" />')";
		_byId("btn-show-game").onclick = function(e){
		 
			showGameCanvas();
		}
		
		_byId("btn-hide-game").onclick = function(e){
			_byId("game-layout").style.display = "none";
			_byId("show-game-wapper").style.display = "block";
		}
		
		
	}
	
	initCanvasEvent();
</script>