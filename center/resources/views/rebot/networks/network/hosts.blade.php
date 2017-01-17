<table class="table mid">
    <thead>
        <tr>
            <td>Sensor</td>
            <td>Vulnerabilities</td>
            <td>Operation</td>
        </tr>
    </thead>
    <tbody>
        <a ui-sref="networks.network.hosts.newSensor">
            <button type="button" class="btn btn-default">
                <span class="glyphicon glyphicon-plus"></span> New Sensor
            </button>
        </a>
        <tr ng-repeat-start="sensor in network.sensors">
            <td>{{sensor.name}}</td>
            <td>{{sensor.vulnerabilities}}</td>
            <td><i class="fa fa-times" aria-hidden="true"></i></td>
        </tr>
        <tr ng-repeat-end>
            <td>Hosts</td>
            <td colspan="5">
                <table class="table mid" style="margin-top: 0">
                    <thead>
                    <tr>
                        <td>Host</td>
                        <td>Vulnerabilities</td>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="host in sensor.hosts">
                        <td>{{host.name}}</td>
                        <td>{{host.vulnerabilities}}</td>
                    </tr>
                    </tbody>
                </table>
            </td>
        </tr>
    </tbody>
</table>