<list>
  <div class="row">

    <div class="seven columns offset-by-five">
      <input class="u-full-width" type="text"
             placeholder="filter triggers" oninput={ filterOut } ref="filter"/>
    </div>

    <table class="u-full-width">
      <thead>
        <tr>
          <th>Trigger</th>
          <th>Name</th>
          <th>Icon</th>
          <th>Response</th>
        </tr>
      </thead>

      <tbody>
        <tr each={ this.listItems }>
          <td>{ trigger }</td>
          <td>{ name }</td>
          <td if={ icon_emoji }>{ icon_emoji }</td>
          <td if={ icon_url }>
            <a href={ icon_url } target="_blank">Show</a>
          </td>
          <td if={ !icon_emoji && !icon_url }><!-- dummy --></td>
          <td>{ substringIfNeed(response_text) } </td>
        </tr>
      </tbody>
    </table>
  </div>

  <script>
   this.dataHandler = opts.handler
   this.listItems = []

   this.on('before-mount', function() {
       this.dataHandler.on('itemUpdated', () => {
           this.filterOut()
           this.update()
       })
   })

   filterOut() {
       let filterWords = this.refs.filter.value
       this.listItems = dataHandler.triggers.filter(v => v.trigger.includes(filterWords))
   }

   substringIfNeed(text) {
       if (text.length > 30) {
           return text.substring(0, 31) + "..."
       } else {
           return [text]
       }
   }

  </script>

  <style>
   .button-group button {
       border-radius: 0;
       float: left;
   }
   .button-group button:first-child { border-radius: 4px 0 0 4px; }
   .button-group button:not(:first-child) { border-left: none; }
   .button-group button:last-child { border-radius: 0 4px 4px 0; }
   .button-selected {
       background-color: #EEE;
       border-color: #AAA;
   }
   table { table-layout: fixed; }
   td { vertical-align: top; }
   tr td:last-child { word-break: break-all; }
  </style>
</list>
