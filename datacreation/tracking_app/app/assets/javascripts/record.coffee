events = 'abort afterprint beforeprint beforeunload blur canplay canplaythrough change click contextmenu copy cuechange cut dblclick DOMContentLoaded drag dragend dragenter dragleave dragover dragstart drop durationchange emptied ended error focus focusin focusout formchange forminput hashchange input invalid keydown keypress keyup load loadeddata loadedmetadata loadstart message mousedown mouseenter mouseleave mousemove mouseout mouseover mouseup mousewheel offline online pagehide pageshow paste pause play playing popstate progress ratechange readystatechange redo reset resize scroll seeked seeking select show stalled storage submit suspend timeupdate undo unload volumechange waiting'


send_to_server = (e) ->
        content = ("\"#{key}\" : \"#{value}\"" for own key, value of e);
        mydata = "{#{content.join(", ")}}"        
        $.ajax({
                url: "/messages",
                type: 'PUT',
                data: mydata,
                contentType: "text/plain",
                processData: false
                })
                                
                                                                                
current_time = -> +new Date()

$(window).bind(events, send_to_server)

                
