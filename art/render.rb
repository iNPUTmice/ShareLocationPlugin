#!/bin/env ruby
resolutions={
	'mdpi'=> 1,
	'hdpi' => 1.5,
	'xhdpi' => 2,
	'xxhdpi' => 3,
	'xxxhdpi' => 4,
	}
images = {
	'ic_launcher.svg' => ['ic_launcher', 48],
	}
images.each do |source, result|
	resolutions.each do |name, factor|
		size = factor * result[1]
		path = "../src/main/res/drawable-#{name}/#{result[0]}.png"
		cmd = "inkscape -e #{path} -C -h #{size} -w #{size} #{source}"
		puts cmd
		system cmd
	end
end
